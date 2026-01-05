package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.collections.joinToString
import kotlin.collections.map

/**
 * Symbol processor that generates loader classes for configurations annotated with @KtConfig.
 * This processor handles the code generation for configuration classes by creating corresponding loader implementations.
 */
class KtConfigSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    companion object {
        private const val KT_CONFIG = "dev.s7a.ktconfig.KtConfig"
    }

    /**
     * Processes all classes annotated with @KtConfig and generates their corresponding loader classes.
     * @param resolver The symbol resolver to find annotated classes
     * @return Empty list as all symbols are processed in a single round
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(KT_CONFIG)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { it.accept(Visitor(), Unit) }
        return emptyList()
    }

    private inner class Visitor : KSVisitorVoid() {
        private val loaderClassName = ClassName("dev.s7a.ktconfig", "KtConfigLoader")
        private val serializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer")
        private val keyableSerializerClassName = ClassName("dev.s7a.ktconfig.serializer.Serializer", "Keyable")
        private val yamlConfigurationClassName = ClassName("org.bukkit.configuration.file", "YamlConfiguration")
        private val stringClassName = ClassName("kotlin", "String")
        private val mapClassName = ClassName("kotlin.collections", "Map")
        private val anyClassName = ClassName("kotlin", "Any")
        private val stringSerializerClassName = ClassName("dev.s7a.ktconfig.serializer", "StringSerializer")
        private val notFoundValueExceptionClassName = ClassName("dev.s7a.ktconfig.exception", "NotFoundValueException")
        private val invalidDiscriminatorExceptionClassName = ClassName("dev.s7a.ktconfig.exception", "InvalidDiscriminatorException")

        /**
         * Visits each class declaration and generates a corresponding loader class.
         * Processes the class's primary constructor parameters to create load/save implementations.
         */
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            // Get @KtConfig annotation
            val ktConfig = classDeclaration.annotations.getKtConfig()
            if (ktConfig == null) {
                logger.error("Classes must be annotated with @KtConfig", classDeclaration)
                return
            }

            val packageName = classDeclaration.packageName.asString()
            val fullName = getFullName(classDeclaration)
            val className = ClassName(packageName, fullName)
            val loaderSimpleName = getLoaderName(classDeclaration)

            val file =
                classDeclaration.containingFile
                    ?: throw IllegalStateException(
                        "Containing file not found for class declaration: ${classDeclaration.simpleName.asString()}",
                    )

            FileSpec
                .builder(packageName, loaderSimpleName)
                .apply {
                    addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "ktlint").build())

                    // sealed interface/class
                    val sealedSubclasses = classDeclaration.getSealedSubclasses().toList()
                    if (sealedSubclasses.isNotEmpty()) {
                        return@apply addSealedLoader(classDeclaration, className, loaderSimpleName, file, ktConfig, sealedSubclasses)
                    }

                    // default
                    addDefaultLoader(classDeclaration, className, loaderSimpleName, file, ktConfig)
                }.build()
                .writeTo(codeGenerator, false)
        }

        /**
         * Generates a default loader class for non-sealed configuration classes.
         * Creates implementations for load, save, decode, and encode functions that handle
         * serialization and deserialization of configuration properties.
         *
         * @param classDeclaration The configuration class declaration to generate a loader for
         * @param className The fully qualified class name
         * @param loaderSimpleName The name for the generated loader class
         * @param file The source file containing the class
         * @param ktConfig The KtConfig annotation configuration including default value settings
         */
        private fun FileSpec.Builder.addDefaultLoader(
            classDeclaration: KSClassDeclaration,
            className: ClassName,
            loaderSimpleName: String,
            file: KSFile,
            ktConfig: KtConfigAnnotation,
        ) {
            val parameters = getParameters(classDeclaration) ?: return

            // Add properties for nested type serializer classes like ListOfString
            addInitializableSerializerProperties(parameters)

            addType(
                TypeSpec
                    .objectBuilder(loaderSimpleName)
                    .addOriginatingKSFile(file)
                    .superclass(loaderClassName.parameterizedBy(className))
                    .apply {
                        if (ktConfig.hasDefault) {
                            addProperty(
                                PropertySpec
                                    .builder("defaultValue", className)
                                    .addModifiers(KModifier.PRIVATE)
                                    .initializer("%T()", className)
                                    .build(),
                            )
                        }
                    }.addLoadFunSpec(className) {
                        addCode(
                            "return %T(\n%L)",
                            className,
                            buildCodeBlock {
                                parameters.forEach { parameter ->
                                    if (ktConfig.hasDefault) {
                                        addStatement(
                                            $$"%L.get(configuration, \"${parentPath}%L\") ?: defaultValue.%N,",
                                            parameter.serializer.ref,
                                            parameter.pathName,
                                            parameter.name,
                                        )
                                    } else {
                                        addStatement(
                                            $$"%L.%N(configuration, \"${parentPath}%L\"),",
                                            parameter.serializer.ref,
                                            parameter.serializer.getFn,
                                            parameter.pathName,
                                        )
                                    }
                                }
                            },
                        )
                    }.addSaveFunSpec(classDeclaration, className) {
                        parameters.forEach { parameter ->
                            addStatement(
                                $$"%L.set(configuration, \"${parentPath}%L\", value.%N)",
                                parameter.serializer.ref,
                                parameter.pathName,
                                parameter.name,
                            )

                            val comment = parameter.comment
                            if (comment != null) {
                                // Add property comment
                                addStatement(
                                    $$"setComment(configuration, \"${parentPath}%L\", listOf(%L))",
                                    parameter.pathName,
                                    comment.joinToString { "\"${it}\"" },
                                )
                            }
                        }
                    }.addDecodeFunSpec(className) {
                        addCode(
                            "return %T(\n%L)",
                            className,
                            buildCodeBlock {
                                parameters.forEach { parameter ->
                                    when {
                                        ktConfig.hasDefault -> {
                                            addStatement(
                                                "value[%S]?.let(%L::deserialize) ?: defaultValue.%N,",
                                                parameter.pathName,
                                                parameter.serializer.ref,
                                                parameter.name,
                                            )
                                        }

                                        parameter.isNullable -> {
                                            addStatement(
                                                "value[%S]?.let(%L::deserialize),",
                                                parameter.pathName,
                                                parameter.serializer.ref,
                                            )
                                        }

                                        else -> {
                                            addStatement(
                                                "value[%S]?.let(%L::deserialize) ?: throw %L(%S),",
                                                parameter.pathName,
                                                parameter.serializer.ref,
                                                notFoundValueExceptionClassName,
                                                parameter.pathName,
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }.addEncodeFunSpec(className) {
                        addCode(
                            "return mapOf(\n%L)",
                            buildCodeBlock {
                                parameters.forEach { parameter ->
                                    if (parameter.isNullable) {
                                        addStatement(
                                            "%S to value.%L?.let(%L::serialize),",
                                            parameter.pathName,
                                            parameter.name,
                                            parameter.serializer.ref,
                                        )
                                    } else {
                                        addStatement(
                                            "%S to %L.serialize(value.%N),",
                                            parameter.pathName,
                                            parameter.serializer.ref,
                                            parameter.name,
                                        )
                                    }
                                }
                            },
                        )
                    }.build(),
            )
        }

        /**
         * Generates a loader class for sealed interfaces/classes.
         * Creates a loader that handles polymorphic deserialization based on a discriminator field.
         *
         * @param classDeclaration The sealed class or interface declaration
         * @param className The fully qualified class name
         * @param loaderSimpleName The name for the generated loader class
         * @param file The source file containing the class
         * @param ktConfig The KtConfig annotation configuration
         * @param sealedSubclasses List of sealed subclasses to support in the loader
         */
        private fun FileSpec.Builder.addSealedLoader(
            classDeclaration: KSClassDeclaration,
            className: ClassName,
            loaderSimpleName: String,
            file: KSFile,
            ktConfig: KtConfigAnnotation,
            sealedSubclasses: List<KSClassDeclaration>,
        ) {
            val sealedSubclassDiscriminators =
                sealedSubclasses.associateWith { subclass ->
                    val discriminators = subclass.getSealedSubclasses().toList().map { getDiscriminator(it) ?: return }
                    if (discriminators.isEmpty()) {
                        Discriminator.Root(getDiscriminator(subclass) ?: return)
                    } else {
                        Discriminator.Childs(discriminators)
                    }
                }

            addType(
                TypeSpec
                    .objectBuilder(loaderSimpleName)
                    .addOriginatingKSFile(file)
                    .superclass(loaderClassName.parameterizedBy(className))
                    .addLoadFunSpec(className) {
                        addStatement(
                            $$"return when (val discriminator = %L.getOrThrow(configuration, \"${parentPath}$${ktConfig.discriminator}\")) {\n%L}",
                            stringSerializerClassName,
                            buildCodeBlock {
                                sealedSubclassDiscriminators.forEach { (subclass, discriminator) ->
                                    addStatement(
                                        "%L -> %L.load(configuration, parentPath)",
                                        discriminator.getAll().joinToCode(",") { buildCodeBlock { add("%S", it) } },
                                        ClassName(packageName, getLoaderName(subclass)),
                                    )
                                }
                                addStatement(
                                    "else -> throw %L(discriminator)",
                                    invalidDiscriminatorExceptionClassName,
                                )
                            },
                        )
                    }.addSaveFunSpec(classDeclaration, className) {
                        addCode(
                            buildCodeBlock {
                                beginControlFlow("when (value)")
                                sealedSubclassDiscriminators.forEach { (subclass, discriminator) ->
                                    val subclassName = ClassName(subclass.packageName.asString(), getFullName(subclass))

                                    add(
                                        buildCodeBlock {
                                            beginControlFlow("is %L ->", subclassName)
                                            if (discriminator is Discriminator.Root) {
                                                addStatement(
                                                    $$"%L.set(configuration, \"${parentPath}%L\", %S)",
                                                    stringSerializerClassName,
                                                    ktConfig.discriminator,
                                                    discriminator.value,
                                                )
                                            }
                                            addStatement(
                                                "%L.save(configuration, value, parentPath)",
                                                ClassName(packageName, getLoaderName(subclass)),
                                            )
                                            endControlFlow()
                                        },
                                    )
                                }
                                endControlFlow()
                            },
                        )
                    }.addDecodeFunSpec(className) {
                        addStatement(
                            $$"return when (val discriminator = value[\"$${ktConfig.discriminator}\"]?.let(%L::deserialize) ?: throw %L(%S)) {\n%L}",
                            stringSerializerClassName,
                            notFoundValueExceptionClassName,
                            ktConfig.discriminator,
                            buildCodeBlock {
                                sealedSubclassDiscriminators.forEach { (subclass, discriminator) ->
                                    addStatement(
                                        "%L -> %L.decode(value)",
                                        discriminator.getAll().joinToCode(",") { buildCodeBlock { add("%S", it) } },
                                        ClassName(packageName, getLoaderName(subclass)),
                                    )
                                }
                                addStatement(
                                    "else -> throw %L(discriminator)",
                                    invalidDiscriminatorExceptionClassName,
                                )
                            },
                        )
                    }.addEncodeFunSpec(className) {
                        addCode(
                            buildCodeBlock {
                                beginControlFlow("return when (value)")
                                sealedSubclassDiscriminators.forEach { (subclass, discriminator) ->
                                    val subclassName = ClassName(subclass.packageName.asString(), getFullName(subclass))

                                    add(
                                        buildCodeBlock {
                                            beginControlFlow("is %L ->", subclassName)
                                            if (discriminator is Discriminator.Root) {
                                                addStatement(
                                                    "mapOf(\"${ktConfig.discriminator}\" to %L.serialize(%S)) +",
                                                    stringSerializerClassName,
                                                    discriminator.value,
                                                )
                                            }
                                            addStatement(
                                                "%L.encode(value)",
                                                ClassName(packageName, getLoaderName(subclass)),
                                            )
                                            endControlFlow()
                                        },
                                    )
                                }
                                endControlFlow()
                            },
                        )
                    }.build(),
            )
        }

        /**
         * Adds a load function to the TypeSpec builder by creating and adding the function specification.
         * This is a convenience wrapper around [createLoadFunSpec].
         *
         * @param className The fully qualified class name to return from the load function
         * @param block Additional configuration for the function builder
         * @return This TypeSpec.Builder for chaining
         */
        private fun TypeSpec.Builder.addLoadFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = addFunction(createLoadFunSpec(className, block))

        /**
         * Creates a load function specification that deserializes configuration data into a class instance.
         * This function reads values from a YamlConfiguration using the parent path as a prefix.
         *
         * @param className The fully qualified class name to return from the load function
         * @param block Additional configuration for the function builder
         * @return A function specification for the load method
         */
        private fun createLoadFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = FunSpec
            .builder("load")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("configuration", yamlConfigurationClassName))
            .addParameter(ParameterSpec("parentPath", stringClassName))
            .apply(block)
            .returns(className)
            .build()

        /**
         * Adds a save function to the TypeSpec builder by creating and adding the function specification.
         * This is a convenience wrapper around [createSaveFunSpec].
         *
         * @param classDeclaration The source class declaration to extract annotations from
         * @param className The fully qualified class name to accept as a parameter
         * @param block Additional configuration for the function builder
         * @return This TypeSpec.Builder for chaining
         */
        private fun TypeSpec.Builder.addSaveFunSpec(
            classDeclaration: KSClassDeclaration,
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = addFunction(createSaveFunSpec(classDeclaration, className, block))

        /**
         * Creates a save function specification that serializes a class instance into configuration data.
         * This function writes values to a YamlConfiguration using the parent path as a prefix,
         * and handles header comments from the class declaration if present.
         *
         * @param classDeclaration The source class declaration to extract annotations from
         * @param className The fully qualified class name to accept as a parameter
         * @param block Additional configuration for the function builder
         * @return A function specification for the save method
         */
        private fun createSaveFunSpec(
            classDeclaration: KSClassDeclaration,
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = FunSpec
            .builder("save")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("configuration", yamlConfigurationClassName))
            .addParameter(ParameterSpec("value", className))
            .addParameter(ParameterSpec("parentPath", stringClassName))
            .apply {
                // Get header comment
                val headerComment = classDeclaration.annotations.getComment()

                if (headerComment != null) {
                    // Add header comment
                    addStatement(
                        "setHeaderComment(configuration, parentPath, listOf(%L))",
                        headerComment.joinToString { "\"${it}\"" },
                    )
                }
            }.apply(block)
            .build()

        /**
         * Adds a decode function to the TypeSpec builder by creating and adding the function specification.
         * This is a convenience wrapper around [createDecodeFunSpec].
         *
         * @param className The fully qualified class name to return from the decode function
         * @param block Additional configuration for the function builder
         * @return This TypeSpec.Builder for chaining
         */
        private fun TypeSpec.Builder.addDecodeFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = addFunction(createDecodeFunSpec(className, block))

        /**
         * Creates a decode function specification that deserializes a map into a class instance.
         * This function converts a map of string keys to nullable values into the target class type,
         * validating required fields and handling nullable values appropriately.
         *
         * @param className The fully qualified class name to return from the decode function
         * @param block Additional configuration for the function builder
         * @return A function specification for the decode method
         */
        private fun createDecodeFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = FunSpec
            .builder("decode")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(
                ParameterSpec(
                    "value",
                    mapClassName.parameterizedBy(stringClassName, anyClassName.copy(nullable = true)),
                ),
            ).apply(block)
            .returns(className)
            .build()

        /**
         * Adds an encode function to the TypeSpec builder by creating and adding the function specification.
         * This is a convenience wrapper around [createEncodeFunSpec].
         *
         * @param className The fully qualified class name to accept as a parameter
         * @param block Additional configuration for the function builder
         * @return This TypeSpec.Builder for chaining
         */
        private fun TypeSpec.Builder.addEncodeFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = addFunction(createEncodeFunSpec(className, block))

        /**
         * Creates an encode function specification that serializes a class instance into a map.
         * This function converts the target class into a map with string keys and nullable values,
         * preserving the structure for configuration persistence.
         *
         * @param className The fully qualified class name to accept as a parameter
         * @param block Additional configuration for the function builder
         * @return A function specification for the encode method
         */
        private fun createEncodeFunSpec(
            className: ClassName,
            block: FunSpec.Builder.() -> Unit,
        ) = FunSpec
            .builder("encode")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("value", className))
            .apply(block)
            .returns(mapClassName.parameterizedBy(stringClassName, anyClassName.copy(nullable = true)))
            .build()

        /**
         * Adds property declarations for serializers that require initialization.
         * Extracts nested type serializers (like ListOfString) from parameters and creates
         * private properties for them in the generated loader class.
         *
         * @param parameters List of configuration parameters that may contain nested serializers
         */
        private fun FileSpec.Builder.addInitializableSerializerProperties(parameters: List<Parameter>) {
            parameters
                .map(Parameter::serializer)
                .extractInitializableSerializers()
                .forEach { serializer ->
                    val className = if (serializer.keyable) keyableSerializerClassName else serializerClassName
                    addProperty(
                        PropertySpec
                            .builder(serializer.uniqueName, className.parameterizedBy(serializer.type))
                            .addModifiers(KModifier.PRIVATE)
                            .initializer("%L", serializer.initialize)
                            .build(),
                    )
                }
        }

        /**
         * Gets the full name of a class by traversing its parent hierarchy.
         * For nested classes, returns a list of class names from outermost to innermost.
         * For top-level classes, returns a single-element list with the class name.
         *
         * @param declaration The class declaration to get the full name for
         * @return List of class names representing the full hierarchy
         */
        private fun getFullName(declaration: KSClassDeclaration): List<String> =
            if (declaration.parent is KSFile) {
                listOf(declaration.simpleName.asString())
            } else {
                getFullName(declaration.parent as KSClassDeclaration) + declaration.simpleName.asString()
            }

        private fun getParameters(declaration: KSClassDeclaration): List<Parameter>? {
            // Get primary constructor from data class
            val primaryConstructor = declaration.primaryConstructor
            if (primaryConstructor == null) {
                logger.error("Classes annotated with @KtConfig must have a primary constructor", declaration)
                return null
            }

            // Get parameters from data class constructor
            return primaryConstructor.parameters.map { createParameter(it) ?: return null }
        }

        /**
         * Determines the discriminator value for a sealed class subclass.
         * The discriminator is used to identify which subclass to deserialize when loading sealed types.
         *
         * First checks for a @SerialName annotation on the class declaration and uses that value if present.
         * If no @SerialName is found, falls back to using the class's fully qualified name as the discriminator.
         *
         * @param declaration The sealed class subclass declaration to get the discriminator for
         * @return The discriminator string (from @SerialName or qualified name), or null if the class has no qualified name
         */
        private fun getDiscriminator(declaration: KSClassDeclaration): String? {
            val serialName = declaration.annotations.getSerialName()
            if (serialName != null) {
                return serialName
            }

            val qualifiedName = declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Class declaration must have a qualified name", declaration)
                return null
            }
            return qualifiedName
        }

        /**
         * Generates a loader class name for the given class declaration.
         * Combines the full class name parts with underscores and appends "Loader".
         *
         * @param declaration The class declaration to generate a loader name for
         * @return The generated loader class name
         */
        private fun getLoaderName(declaration: KSClassDeclaration): String {
            val fullName = getFullName(declaration).joinToString("")
            return "${fullName}Loader"
        }

        /**
         * Extracts comment content from @Comment annotations in the sequence.
         * Processes the annotation arguments to get the comment strings.
         *
         * @return List of comment strings, or null if no valid comment annotation is found
         */
        private fun Sequence<KSAnnotation>.getComment(): List<String>? {
            forEach { annotation ->
                if (annotation.shortName.asString() == "Comment") {
                    val content = annotation.arguments.firstOrNull { it.name?.asString() == "content" }
                    if (content != null) {
                        val value = content.value
                        if (value is List<*> && value.isNotEmpty() && value.first() is String) {
                            return value.map(Any?::toString)
                        }
                    }
                }
            }

            return null
        }

        /**
         * Extracts the path name from @PathName annotations in the sequence.
         * Processes the annotation arguments to get the path name string.
         *
         * @return The path name string from the annotation, or null if no valid @PathName annotation is found
         */
        private fun Sequence<KSAnnotation>.getPathName(): String? {
            forEach { annotation ->
                if (annotation.shortName.asString() == "PathName") {
                    val content = annotation.arguments.firstOrNull { it.name?.asString() == "name" }
                    if (content != null) {
                        val value = content.value
                        if (value is String) {
                            return value
                        }
                    }
                }
            }

            return null
        }

        /**
         * Extracts the serial name from @SerialName annotations in the sequence.
         * The @SerialName annotation is used to specify a custom serialization name for sealed class subclasses.
         *
         * @return The serial name string from the annotation, or null if no valid @SerialName annotation is found
         */
        private fun Sequence<KSAnnotation>.getSerialName(): String? {
            forEach { annotation ->
                if (annotation.shortName.asString() == "SerialName") {
                    val content = annotation.arguments.firstOrNull { it.name?.asString() == "name" }
                    if (content != null) {
                        val value = content.value
                        if (value is String) {
                            return value
                        }
                    }
                }
            }

            return null
        }

        /**
         * Checks if the sequence contains a @KtConfig annotation.
         *
         * @return True if @KtConfig annotation is present, false otherwise
         */
        private fun Sequence<KSAnnotation>.getKtConfig(): KtConfigAnnotation? =
            firstNotNullOfOrNull { annotation ->
                if (annotation.shortName.asString() != "KtConfig") return@firstNotNullOfOrNull null
                val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
                KtConfigAnnotation(
                    hasDefault = arguments["hasDefault"] as? Boolean ?: false,
                    discriminator = (arguments["discriminator"] as? String).orEmpty().ifBlank { "$" },
                )
            }

        /**
         * Creates a Parameter object from a KSValueParameter, validating the parameter name and type.
         * Returns null if the parameter is invalid or unsupported.
         */
        private fun createParameter(declaration: KSValueParameter): Parameter? {
            val name = declaration.name?.asString()
            if (name == null) {
                logger.error("Primary constructor parameters must have a name", declaration)
                return null
            }

            val serializer = getSerializer(declaration) ?: return null
            val pathName = declaration.annotations.getPathName()
            val comment = declaration.annotations.getComment()
            return Parameter(pathName ?: name, name, serializer, comment)
        }

        private fun Sequence<KSAnnotation>.getSerializer(): Serializer.Custom? {
            forEach { annotation ->
                if (annotation.shortName.asString() == "UseSerializer") {
                    val serializer = annotation.arguments.firstOrNull { it.name?.asString() == "serializer" }
                    if (serializer != null) {
                        val value = serializer.value
                        if (value is KSType) {
                            val qualifiedName = value.declaration.qualifiedName
                            if (qualifiedName != null) {
                                return Serializer.Custom(qualifiedName.asString())
                            }
                        }
                    }
                }
            }

            return null
        }

        private fun KSType.solveTypeAlias(): Pair<KSType, Serializer.Custom?> {
            val declaration = this.declaration
            val serializer =
                // Get typealias-annotated serializer
                annotations.getSerializer()
                    ?: // Get class-annotated serializer
                    declaration.annotations.getSerializer()

            // Solve typealias
            if (declaration is KSTypeAlias) {
                val (resolvedType, resolvedSerializer) = declaration.type.resolve().solveTypeAlias()
                return resolvedType to (resolvedSerializer ?: serializer)
            }

            return this to serializer
        }

        private fun getSerializer(declaration: KSValueParameter): Parameter.Serializer? = getSerializer(declaration.type.resolve())

        private fun getSerializer(declaration: KSTypeArgument): Parameter.Serializer? {
            val type = declaration.type
            if (type == null) {
                logger.error("Type argument must have a type", declaration)
                return null
            }
            return getSerializer(type.resolve())
        }

        private fun getSerializer(type: KSType): Parameter.Serializer? {
            val (solvedType, customSerializer) = type.solveTypeAlias()
            return getSerializer(solvedType, customSerializer)
        }

        /**
         * Resolves the appropriate serializer for a given parameter type.
         * Handles both simple types and generic collections, returning null for unsupported types.
         */
        private fun getSerializer(
            type: KSType,
            customSerializer: Serializer.Custom?,
        ): Parameter.Serializer? {
            // Get qualifiedName, className
            val declaration = type.declaration
            val qualifiedName = declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Type must have a qualified name", declaration)
                return null
            }
            val className = ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))

            // Handle enum class, value class
            val modifiers = declaration.modifiers
            when {
                modifiers.contains(Modifier.ENUM) -> {
                    return Parameter.Serializer.EnumClass(className, type.isMarkedNullable, qualifiedName)
                }

                modifiers.contains(Modifier.VALUE) -> {
                    if (declaration !is KSClassDeclaration) {
                        logger.error("Value classes must be classes", declaration)
                        return null
                    }

                    val primaryConstructor = declaration.primaryConstructor
                    if (primaryConstructor == null) {
                        logger.error("Value classes must have a primary constructor", declaration)
                        return null
                    }

                    val parameter = primaryConstructor.parameters.singleOrNull()
                    if (parameter == null) {
                        logger.error("Value classes must have a single parameter", declaration)
                        return null
                    }

                    val parameterName = parameter.name?.asString()
                    if (parameterName == null) {
                        logger.error("Value class parameter must have a name", declaration)
                        return null
                    }

                    val serializer = getSerializer(parameter) ?: return null

                    return Parameter.Serializer.ValueClass(className, type.isMarkedNullable, parameterName, serializer)
                }
            }

            // Get serializer
            val serializer = customSerializer ?: findSerializer(qualifiedName, type)
            if (serializer == null) {
                logger.error("Unsupported type: $qualifiedName", declaration)
                return null
            }

            // Check for generic type arguments and create Class serializer if present
            val isNullable = type.isMarkedNullable

            when (serializer) {
                Serializer.ConfigurationSerializable -> {
                    return Parameter.Serializer.ConfigurationSerializableClass(className, isNullable, qualifiedName)
                }

                is Serializer.BuiltIn -> {
                    return Parameter.Serializer.Object(className, isNullable, serializer.name, serializer.qualifiedName)
                }

                is Serializer.Collection -> {
                    val arguments = type.arguments
                    if (arguments.isNotEmpty()) {
                        val argumentSerializers =
                            arguments.map { argument ->
                                getSerializer(argument) ?: return null
                            }

                        val nullableValue =
                            arguments
                                .last()
                                .type
                                ?.resolve()
                                ?.isMarkedNullable == true
                        return Parameter.Serializer.Class(
                            className,
                            isNullable,
                            serializer.name,
                            serializer.qualifiedName,
                            argumentSerializers,
                            serializer.supportNullableValue && nullableValue,
                        )
                    }

                    return Parameter.Serializer.Object(className, isNullable, serializer.name, serializer.qualifiedName)
                }

                is Serializer.Nested -> {
                    return Parameter.Serializer.Nested(
                        className,
                        isNullable,
                        serializer.qualifiedName,
                        serializer.loaderName,
                    )
                }

                is Serializer.Custom -> {
                    return Parameter.Serializer.Object(
                        className,
                        isNullable,
                        serializer.qualifiedName.replace('.', '_'), // unique name
                        serializer.qualifiedName,
                    )
                }
            }
        }

        private val serializers =
            mapOf(
                // Primitive
                "kotlin.Byte" to Serializer.BuiltIn("Byte"),
                "kotlin.Char" to Serializer.BuiltIn("Char"),
                "kotlin.Int" to Serializer.BuiltIn("Int"),
                "kotlin.Long" to Serializer.BuiltIn("Long"),
                "kotlin.Short" to Serializer.BuiltIn("Short"),
                "kotlin.String" to Serializer.BuiltIn("String"),
                "kotlin.UByte" to Serializer.BuiltIn("UByte"),
                "kotlin.UInt" to Serializer.BuiltIn("UInt"),
                "kotlin.ULong" to Serializer.BuiltIn("ULong"),
                "kotlin.UShort" to Serializer.BuiltIn("UShort"),
                "kotlin.Double" to Serializer.BuiltIn("Double"),
                "kotlin.Float" to Serializer.BuiltIn("Float"),
                "kotlin.Boolean" to Serializer.BuiltIn("Boolean"),
                // Common
                "java.util.UUID" to Serializer.BuiltIn("UUID"),
                "java.time.Instant" to Serializer.BuiltIn("Instant"),
                "java.time.LocalTime" to Serializer.BuiltIn("LocalTime"),
                "java.time.LocalDate" to Serializer.BuiltIn("LocalDate"),
                "java.time.LocalDateTime" to Serializer.BuiltIn("LocalDateTime"),
                "java.time.Year" to Serializer.BuiltIn("Year"),
                "java.time.YearMonth" to Serializer.BuiltIn("YearMonth"),
                "java.time.OffsetTime" to Serializer.BuiltIn("OffsetTime"),
                "java.time.OffsetDateTime" to Serializer.BuiltIn("OffsetDateTime"),
                "java.time.ZonedDateTime" to Serializer.BuiltIn("ZonedDateTime"),
                "java.time.Duration" to Serializer.BuiltIn("Duration"),
                "java.time.Period" to Serializer.BuiltIn("Period"),
                // Collections
                "kotlin.Array" to Serializer.Collection("Array", true),
                "kotlin.ByteArray" to Serializer.Collection("ByteArray", false),
                "kotlin.CharArray" to Serializer.Collection("CharArray", false),
                "kotlin.IntArray" to Serializer.Collection("IntArray", false),
                "kotlin.LongArray" to Serializer.Collection("LongArray", false),
                "kotlin.ShortArray" to Serializer.Collection("ShortArray", false),
                "kotlin.UByteArray" to Serializer.Collection("UByteArray", false),
                "kotlin.UIntArray" to Serializer.Collection("UIntArray", false),
                "kotlin.ULongArray" to Serializer.Collection("ULongArray", false),
                "kotlin.UShortArray" to Serializer.Collection("UShortArray", false),
                "kotlin.DoubleArray" to Serializer.Collection("DoubleArray", false),
                "kotlin.FloatArray" to Serializer.Collection("FloatArray", false),
                "kotlin.BooleanArray" to Serializer.Collection("BooleanArray", false),
                "kotlin.collections.List" to Serializer.Collection("List", true),
                "kotlin.collections.Set" to Serializer.Collection("Set", true),
                "kotlin.collections.ArrayDeque" to Serializer.Collection("ArrayDeque", true),
                "kotlin.collections.Map" to Serializer.Collection("Map", true),
            )

        /**
         * Finds the appropriate serializer name for a given type.
         * First checks if the type implements ConfigurationSerializable,
         * then looks up built-in serializers.
         *
         * @param qualifiedName The fully qualified name of the type
         * @param type The KSType representing the type to find a serializer for
         * @return The name of the serializer to use, or null if no suitable serializer is found
         */
        private fun findSerializer(
            qualifiedName: String,
            type: KSType,
        ): Serializer? {
            val declaration = type.declaration
            if (declaration is KSClassDeclaration) {
                // Check if type marked @KtConfig
                val ktConfig = declaration.annotations.getKtConfig()
                if (ktConfig != null) {
                    return Serializer.Nested(qualifiedName, "${declaration.packageName.asString()}.${getLoaderName(declaration)}")
                }

                // Check if type implements ConfigurationSerializable
                declaration.getAllSuperTypes().forEach { superType ->
                    val qualifiedName = superType.declaration.qualifiedName?.asString()
                    if (qualifiedName == "org.bukkit.configuration.serialization.ConfigurationSerializable") {
                        return Serializer.ConfigurationSerializable
                    }
                }
            }

            // Lookup serializer name from the predefined map of built-in serializers
            return serializers[qualifiedName]
        }

        /**
         * Resolves a list of serializers by flattening nested serializers and removing duplicates.
         *
         * @return A flattened list of unique initializable serializers identified by their unique names that need initialization
         */
        private fun List<Parameter.Serializer>.extractInitializableSerializers(): List<Parameter.Serializer.InitializableSerializer> =
            filterIsInstance<Parameter.Serializer.InitializableSerializer>()
                .flatMap {
                    when (it) {
                        is Parameter.Serializer.Class -> {
                            it.arguments.extractInitializableSerializers() + it
                        }

                        is Parameter.Serializer.Nested -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.ConfigurationSerializableClass -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.EnumClass -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.ValueClass -> {
                            listOf(it.argument).extractInitializableSerializers() + it
                        }
                    }
                }.distinctBy(Parameter.Serializer::uniqueName)
    }

    private data class KtConfigAnnotation(
        val hasDefault: Boolean,
        val discriminator: String,
    )

    private sealed interface Discriminator {
        fun getAll(): List<String>

        data class Root(
            val value: String,
        ) : Discriminator {
            override fun getAll() = listOf(value)
        }

        data class Childs(
            val values: List<String>,
        ) : Discriminator {
            override fun getAll() = values
        }
    }

    private data class Parameter(
        val pathName: String,
        val name: String,
        val serializer: Serializer,
        val comment: List<String>?,
    ) {
        val isNullable
            get() = serializer.isNullable

        sealed class Serializer(
            val type: TypeName,
            val isNullable: Boolean,
        ) {
            abstract val uniqueName: String
            abstract val ref: String
            abstract val keyable: Boolean

            val getFn = if (isNullable) "get" else "getOrThrow"

            class Object(
                type: ClassName,
                isNullable: Boolean,
                name: String,
                qualifiedName: String,
            ) : Serializer(type, isNullable) {
                override val uniqueName = name
                override val ref = qualifiedName
                override val keyable = true
            }

            sealed class InitializableSerializer(
                type: TypeName,
                isNullable: Boolean,
                name: String,
                protected val qualifiedName: String = "dev.s7a.ktconfig.serializer.${name}Serializer",
            ) : Serializer(type, isNullable) {
                abstract val initialize: String
            }

            // Properties like type, uniqueName, ref are stored as class properties
            // to avoid recalculating them each time they are accessed
            class Class(
                parentType: ClassName,
                isNullable: Boolean,
                name: String,
                qualifiedName: String,
                val arguments: List<Serializer>,
                val nullableValue: Boolean,
            ) : InitializableSerializer(
                    parentType.parameterizedBy(
                        arguments.mapIndexed { index, it ->
                            if (arguments.lastIndex == index) {
                                it.type.copy(nullable = nullableValue)
                            } else {
                                it.type
                            }
                        },
                    ),
                    isNullable,
                    name,
                    qualifiedName,
                ) {
                override val uniqueName =
                    buildString {
                        if (nullableValue) append("Nullable")
                        append(name)
                        append("Of")
                        arguments.forEach {
                            append(it.uniqueName)
                        }
                    }
                override val ref = uniqueName
                override val keyable = false
                override val initialize =
                    buildString {
                        append(qualifiedName)
                        if (nullableValue) {
                            append(".Nullable")
                        }
                        append("(${arguments.joinToString(", ") { it.ref }})")
                    }
            }

            class Nested(
                type: ClassName,
                isNullable: Boolean,
                qualifiedName: String,
                loaderName: String,
            ) : InitializableSerializer(type, isNullable, "Nested") {
                override val uniqueName = qualifiedName.replace('.', '_')
                override val ref = uniqueName
                override val keyable = false
                override val initialize = "${super.qualifiedName}($loaderName)"
            }

            class ConfigurationSerializableClass(
                type: ClassName,
                isNullable: Boolean,
                classQualifiedName: String,
            ) : InitializableSerializer(type, isNullable, "ConfigurationSerializable") {
                override val uniqueName = type.canonicalName.replace(".", "_")
                override val ref = uniqueName
                override val keyable = false
                override val initialize = "$qualifiedName<$classQualifiedName>()"
            }

            class EnumClass(
                type: ClassName,
                isNullable: Boolean,
                enumQualifiedName: String,
            ) : InitializableSerializer(type, isNullable, "Enum") {
                override val uniqueName = type.canonicalName.replace(".", "_")
                override val ref = uniqueName
                override val keyable = true
                override val initialize = "$qualifiedName($enumQualifiedName::class.java)"
            }

            class ValueClass(
                type: ClassName,
                isNullable: Boolean,
                parameterName: String,
                val argument: Serializer,
            ) : InitializableSerializer(type, isNullable, "Value") {
                override val uniqueName = type.canonicalName.replace(".", "_")
                override val ref = uniqueName
                override val keyable = argument.keyable
                override val initialize =
                    buildString {
                        append(qualifiedName)
                        if (keyable) append(".Keyable")
                        append("(${argument.ref}, { $type(it) }, { it.$parameterName })")
                    }
            }
        }
    }

    private sealed interface Serializer {
        data object ConfigurationSerializable : Serializer

        data class BuiltIn(
            val name: String,
        ) : Serializer {
            val qualifiedName = "dev.s7a.ktconfig.serializer.${name}Serializer"
        }

        data class Collection(
            val name: String,
            val supportNullableValue: Boolean,
        ) : Serializer {
            val qualifiedName = "dev.s7a.ktconfig.serializer.${name}Serializer"
        }

        data class Nested(
            val qualifiedName: String,
            val loaderName: String,
        ) : Serializer

        data class Custom(
            val qualifiedName: String,
        ) : Serializer
    }
}
