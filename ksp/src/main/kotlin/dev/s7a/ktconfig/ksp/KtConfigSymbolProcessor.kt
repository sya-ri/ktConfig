package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.Comment.Companion.getCommentAnnotation
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.Companion.getKtConfigAnnotation
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.SerialName.Companion.getSerialNameAnnotation
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.UseSerializer.Companion.getUseSerializerAnnotation
import dev.s7a.ktconfig.ksp.Serializer.Companion.extractInitializableSerializers
import kotlin.collections.map

private data class LoaderTarget(
    val declaration: KSClassDeclaration,
    val packageName: String,
    val typeName: TypeName,
    val loaderSimpleName: String,
    val file: KSFile,
    val ktConfig: KtConfigAnnotation,
    val typeSubstitutions: Map<String, KSType> = emptyMap(),
    val headerCommentDeclaration: KSClassDeclaration = declaration,
)

private data class SealedSubclassTarget(
    val declaration: KSClassDeclaration,
    val checkTypeName: ClassName,
    val valueTypeName: TypeName,
    val loaderTypeName: ClassName,
    val discriminator: String,
    val requiresValueCast: Boolean,
    val generatedLoaderTarget: LoaderTarget?,
)

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
            .forEach { it.accept(Visitor(resolver), Unit) }
        return emptyList()
    }

    private inner class Visitor(
        private val resolver: Resolver,
    ) : KSVisitorVoid() {
        private val loaderClassName = ClassName("dev.s7a.ktconfig", "KtConfigLoader")
        private val serializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer")
        private val keyableSerializerClassName = ClassName("dev.s7a.ktconfig.serializer.Serializer", "Keyable")
        private val yamlConfigurationClassName = ClassName("org.bukkit.configuration.file", "YamlConfiguration")
        private val stringClassName = ClassName("kotlin", "String")
        private val mapClassName = ClassName("kotlin.collections", "Map")
        private val anyClassName = ClassName("kotlin", "Any")
        private val ktConfigErrorClassName = ClassName("dev.s7a.ktconfig", "KtConfigError")
        private val ktConfigLoadExceptionClassName = ClassName("dev.s7a.ktconfig.exception", "KtConfigLoadException")
        private val stringSerializerClassName = ClassName("dev.s7a.ktconfig.serializer", "StringSerializer")
        private val notFoundValueExceptionClassName = ClassName("dev.s7a.ktconfig.exception", "NotFoundValueException")
        private val invalidDiscriminatorExceptionClassName = ClassName("dev.s7a.ktconfig.exception", "InvalidDiscriminatorException")
        private val generatedLoaderSuppressAnnotation =
            AnnotationSpec
                .builder(Suppress::class)
                .addMember(
                    "%S, %S, %S, %S, %S",
                    "ktlint",
                    "OPT_IN_USAGE",
                    "OPT_IN_USAGE_ERROR",
                    "UNCHECKED_CAST",
                    "USELESS_CAST",
                ).build()

        /**
         * Visits each class declaration and generates a corresponding loader class.
         * Processes the class's primary constructor parameters to create load/save implementations.
         */
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            val ktConfig = classDeclaration.getKtConfigAnnotation()
            if (ktConfig == null) {
                logger.error("Classes must be annotated with @KtConfig", classDeclaration)
                return
            }

            if (classDeclaration.typeParameters.isNotEmpty()) {
                return
            }

            val packageName = classDeclaration.packageName.asString()
            val fullName = getFullName(classDeclaration)
            val className = ClassName(packageName, fullName)
            val loaderSimpleName = getLoaderName(classDeclaration)
            if (loaderSimpleName == null) {
                logger.error("Classes must be annotated with @KtConfig", classDeclaration)
                return
            }

            val file =
                classDeclaration.containingFile
                    ?: throw IllegalStateException(
                        "Containing file not found for class declaration: ${classDeclaration.simpleName.asString()}",
                    )

            generateLoader(
                LoaderTarget(
                    declaration = classDeclaration,
                    packageName = packageName,
                    typeName = className,
                    loaderSimpleName = loaderSimpleName,
                    file = file,
                    ktConfig = ktConfig,
                ),
            )
        }

        override fun visitTypeAlias(
            typeAlias: KSTypeAlias,
            data: Unit,
        ) {
            val ktConfig = typeAlias.getKtConfigAnnotation()
            if (ktConfig == null) {
                logger.error("Type aliases must be annotated with @KtConfig", typeAlias)
                return
            }

            val resolvedType = typeAlias.type.resolve()
            val classDeclaration = resolvedType.declaration as? KSClassDeclaration
            if (classDeclaration == null) {
                logger.error("@KtConfig type aliases must resolve to a class", typeAlias)
                return
            }
            val classKtConfig = classDeclaration.getKtConfigAnnotation()
            if (ktConfig.hasDefault && classKtConfig?.hasDefault != true) {
                logger.warn(
                    "@KtConfig(hasDefault = ...) on type aliases is ignored. Put @KtConfig(hasDefault = ...) on the aliased class instead.",
                    typeAlias,
                )
            }

            val packageName = typeAlias.packageName.asString()
            val className = ClassName(packageName, typeAlias.simpleName.asString())
            val loaderSimpleName = getLoaderName(typeAlias)
            if (loaderSimpleName == null) {
                logger.error("Type aliases must be annotated with @KtConfig", typeAlias)
                return
            }

            val file =
                typeAlias.containingFile
                    ?: throw IllegalStateException(
                        "Containing file not found for type alias: ${typeAlias.simpleName.asString()}",
                    )

            val typeSubstitutions = classDeclaration.typeParameters.toTypeSubstitutions(resolvedType.arguments)
            generateLoader(
                LoaderTarget(
                    declaration = classDeclaration,
                    packageName = packageName,
                    typeName = className,
                    loaderSimpleName = loaderSimpleName,
                    file = file,
                    ktConfig =
                        ktConfig.copy(
                            hasDefault = classKtConfig?.hasDefault ?: false,
                        ),
                    typeSubstitutions = typeSubstitutions,
                ),
            )
        }

        private fun generateLoader(target: LoaderTarget) {
            val addedInitializableSerializerNames = mutableSetOf<String>()
            FileSpec
                .builder(target.packageName, target.loaderSimpleName)
                .apply {
                    addAnnotation(generatedLoaderSuppressAnnotation)

                    val sealedSubclasses = target.declaration.getSealedSubclassesDeeply()
                    if (sealedSubclasses.isNotEmpty()) {
                        val sealedSubclassTargets = sealedSubclasses.flatMap { it.toSealedSubclassTargets(target) }
                        if (sealedSubclassTargets.isEmpty()) {
                            logger.error("No compatible sealed subclasses found for generated loader", target.declaration)
                            return@apply
                        }
                        val generatedSubclassParameters = mutableMapOf<SealedSubclassTarget, List<Parameter>>()
                        sealedSubclassTargets.forEach { subclass ->
                            val generatedTarget = subclass.generatedLoaderTarget ?: return@forEach
                            val parameters = getParameters(generatedTarget.declaration, generatedTarget.typeSubstitutions) ?: return@apply
                            addInitializableSerializerProperties(parameters, addedInitializableSerializerNames)
                            generatedSubclassParameters[subclass] = parameters
                        }
                        return@apply addSealedLoader(target, sealedSubclassTargets, generatedSubclassParameters)
                    }

                    addDefaultLoader(target, addedInitializableSerializerNames)
                }.build()
                .writeTo(codeGenerator, false)
        }

        /**
         * Generates a default loader class for non-sealed configuration classes.
         * Creates implementations for load, save, decode, and encode functions that handle
         * serialization and deserialization of configuration properties.
         *
         * @param target The class, type, loader name, source file, annotation, and type substitutions to generate a loader for
         * @param addedInitializableSerializerNames Serializer property names already added to this generated file
         */
        private fun FileSpec.Builder.addDefaultLoader(
            target: LoaderTarget,
            addedInitializableSerializerNames: MutableSet<String>,
        ) {
            val parameters = getParameters(target.declaration, target.typeSubstitutions) ?: return

            // Add properties for nested type serializer classes like ListOfString
            addInitializableSerializerProperties(parameters, addedInitializableSerializerNames)

            addType(
                TypeSpec
                    .objectBuilder(target.loaderSimpleName)
                    .addOriginatingKSFile(target.file)
                    .superclass(loaderClassName.parameterizedBy(target.typeName))
                    .apply {
                        if (target.ktConfig.hasDefault) {
                            addProperty(
                                PropertySpec
                                    .builder("defaultValue", target.typeName)
                                    .addModifiers(KModifier.PRIVATE)
                                    .initializer("%T()", target.typeName)
                                    .build(),
                            )
                        }
                    }.addLoadFunSpec(target.typeName) {
                        addAggregatingLoadCode(target, parameters, "defaultValue")
                    }.addSaveFunSpec(target.headerCommentDeclaration, target.typeName) {
                        parameters.forEach { parameter ->
                            addStatement(
                                "${parameter.serializer.refKey}.set(configuration, \"%L%L\", value.%N)",
                                parameter.serializer.ref,
                                $$"${parentPath}",
                                parameter.pathName,
                                parameter.name,
                            )

                            val comment = parameter.comment
                            if (comment != null) {
                                // Add property comment
                                addStatement(
                                    "setComment(configuration, \"%L%L\", %L)",
                                    $$"${parentPath}",
                                    parameter.pathName,
                                    comment.asLiteralList(),
                                )
                            }
                        }
                    }.addDecodeFunSpec(target.typeName) {
                        addAggregatingDecodeCode(target, parameters, "defaultValue")
                    }.addEncodeFunSpec(target.typeName) {
                        addCode(
                            "return mapOf(\n%L)",
                            buildCodeBlock {
                                parameters.forEach { parameter ->
                                    if (parameter.isNullable) {
                                        addStatement(
                                            "%S to value.%N?.let(${parameter.serializer.refKey}::serialize),",
                                            parameter.pathName,
                                            parameter.name,
                                            parameter.serializer.ref,
                                        )
                                    } else {
                                        addStatement(
                                            "%S to ${parameter.serializer.refKey}.serialize(value.%N),",
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
         * @param target The sealed class or interface target to generate a loader for
         * @param sealedSubclasses List of sealed subclass targets to support in the loader
         * @param generatedSubclassParameters Constructor parameters for synthetic sealed subclass handling
         */
        private fun FileSpec.Builder.addSealedLoader(
            target: LoaderTarget,
            sealedSubclasses: List<SealedSubclassTarget>,
            generatedSubclassParameters: Map<SealedSubclassTarget, List<Parameter>>,
        ) {
            addType(
                TypeSpec
                    .objectBuilder(target.loaderSimpleName)
                    .addOriginatingKSFile(target.file)
                    .superclass(loaderClassName.parameterizedBy(target.typeName))
                    .apply {
                        generatedSubclassParameters.forEach { (subclass, _) ->
                            val generatedTarget = subclass.generatedLoaderTarget ?: return@forEach
                            if (generatedTarget.ktConfig.hasDefault) {
                                addProperty(
                                    PropertySpec
                                        .builder(generatedTarget.defaultValuePropertyName(), generatedTarget.typeName)
                                        .addModifiers(KModifier.PRIVATE)
                                        .initializer("%T()", generatedTarget.typeName)
                                        .build(),
                                )
                            }
                        }
                    }
                    .addLoadFunSpec(target.typeName) {
                        addControlFlowCode(
                            "return when (val discriminator = %T.getOrThrow(configuration, \"%L%L\"))",
                            stringSerializerClassName,
                            $$"${parentPath}",
                            target.ktConfig.discriminator,
                        ) {
                            sealedSubclasses.forEach { subclass ->
                                addControlFlow("%S ->", subclass.discriminator) {
                                    val generatedTarget = subclass.generatedLoaderTarget
                                    if (generatedTarget == null) {
                                        addStatement("%T.load(configuration, parentPath)", subclass.loaderTypeName)
                                    } else {
                                        addGeneratedLoadExpression(generatedTarget, generatedSubclassParameters.getValue(subclass))
                                    }
                                }
                            }
                            addControlFlow("else ->") {
                                addStatement("throw %T(discriminator)", invalidDiscriminatorExceptionClassName)
                            }
                        }
                    }.addSaveFunSpec(target.declaration, target.typeName) {
                        addControlFlowCode("when (value)") {
                            sealedSubclasses.forEach { subclass ->
                                addSealedSubclassControlFlow(subclass) {
                                    addStatement(
                                        "%T.set(configuration, \"%L%L\", %S)",
                                        stringSerializerClassName,
                                        $$"${parentPath}",
                                        target.ktConfig.discriminator,
                                        subclass.discriminator,
                                    )
                                    val generatedTarget = subclass.generatedLoaderTarget
                                    if (generatedTarget == null) {
                                        addSealedSubclassLoaderStatement(subclass, "save(configuration, %L, parentPath)")
                                    } else {
                                        addGeneratedSaveStatements(generatedTarget, generatedSubclassParameters.getValue(subclass), subclass.valueCode())
                                    }
                                }
                            }
                        }
                    }.addDecodeFunSpec(target.typeName) {
                        addControlFlowCode(
                            "return when (val discriminator = value[%S]?.let(%T::deserialize) ?: throw %T(%S))",
                            target.ktConfig.discriminator,
                            stringSerializerClassName,
                            notFoundValueExceptionClassName,
                            target.ktConfig.discriminator,
                        ) {
                            sealedSubclasses.forEach { subclass ->
                                addControlFlow("%S ->", subclass.discriminator) {
                                    val generatedTarget = subclass.generatedLoaderTarget
                                    if (generatedTarget == null) {
                                        addStatement("%T.decode(value)", subclass.loaderTypeName)
                                    } else {
                                        addGeneratedDecodeExpression(generatedTarget, generatedSubclassParameters.getValue(subclass))
                                    }
                                }
                            }
                            addControlFlow("else ->") {
                                addStatement("throw %T(discriminator)", invalidDiscriminatorExceptionClassName)
                            }
                        }
                    }.addEncodeFunSpec(target.typeName) {
                        addControlFlowCode("return when (value)") {
                            sealedSubclasses.forEach { subclass ->
                                addSealedSubclassControlFlow(subclass) {
                                    val generatedTarget = subclass.generatedLoaderTarget
                                    if (generatedTarget == null) {
                                        addStatement(
                                            "mapOf(%S to %T.serialize(%S)) + %T.encode(%L)",
                                            target.ktConfig.discriminator,
                                            stringSerializerClassName,
                                            subclass.discriminator,
                                            subclass.loaderTypeName,
                                            subclass.valueCode(),
                                        )
                                    } else {
                                        addGeneratedEncodeExpression(
                                            target.ktConfig.discriminator,
                                            subclass.discriminator,
                                            generatedSubclassParameters.getValue(subclass),
                                            subclass.valueCode(),
                                        )
                                    }
                                }
                            }
                        }
                    }.build(),
            )
        }

        private fun CodeBlock.Builder.addGeneratedLoadExpression(
            target: LoaderTarget,
            parameters: List<Parameter>,
        ) {
            add(
                "validateLoadedConfig(%T(\n%L), parentPath)",
                target.typeName,
                buildCodeBlock {
                    parameters.forEach { parameter ->
                        if (target.ktConfig.hasDefault) {
                            addStatement(
                                "${parameter.serializer.refKey}.get(configuration, \"%L%L\") ?: %N.%N,",
                                parameter.serializer.ref,
                                $$"${parentPath}",
                                parameter.pathName,
                                target.defaultValuePropertyName(),
                                parameter.name,
                            )
                        } else {
                            addStatement(
                                "${parameter.serializer.refKey}.%N(configuration, \"%L%L\"),",
                                parameter.serializer.ref,
                                parameter.serializer.getFn,
                                $$"${parentPath}",
                                parameter.pathName,
                            )
                        }
                    }
                },
            )
        }

        private fun FunSpec.Builder.addAggregatingLoadCode(
            target: LoaderTarget,
            parameters: List<Parameter>,
            defaultValueName: String,
        ) {
            addStatement("val _ktConfigErrors = mutableListOf<%T>()", ktConfigErrorClassName)
            parameters.forEachIndexed { index, parameter ->
                val valueName = "_ktConfigValue$index"
                val valueTypeName = parameter.serializer.typeName.copy(nullable = true)
                if (target.ktConfig.hasDefault) {
                    addStatement("var %N: %T = %N.%N", valueName, valueTypeName, defaultValueName, parameter.name)
                } else {
                    addStatement("var %N: %T = null", valueName, valueTypeName)
                }
                addControlFlowCode("try") {
                    when {
                        target.ktConfig.hasDefault -> {
                            addStatement(
                                "${parameter.serializer.refKey}.get(configuration, \"%L%L\")?.let { %N = it }",
                                parameter.serializer.ref,
                                $$"${parentPath}",
                                parameter.pathName,
                                valueName,
                            )
                        }

                        parameter.isNullable -> {
                            addStatement(
                                "%N = ${parameter.serializer.refKey}.get(configuration, \"%L%L\")",
                                valueName,
                                parameter.serializer.ref,
                                $$"${parentPath}",
                                parameter.pathName,
                            )
                        }

                        else -> {
                            addStatement(
                                "%N = ${parameter.serializer.refKey}.getOrThrow(configuration, \"%L%L\")",
                                valueName,
                                parameter.serializer.ref,
                                $$"${parentPath}",
                                parameter.pathName,
                            )
                        }
                    }
                }
                addControlFlowCode("catch (e: %T)", Throwable::class) {
                    addStatement(
                        "_ktConfigErrors += %T.fromException(\"%L%L\", e)",
                        ktConfigErrorClassName,
                        $$"${parentPath}",
                        parameter.pathName,
                    )
                }
            }
            addControlFlowCode("if (_ktConfigErrors.isNotEmpty())") {
                addStatement("throw %T(_ktConfigErrors)", ktConfigLoadExceptionClassName)
            }
            addCode(
                "return validateLoadedConfig(%T(\n%L), parentPath)",
                target.typeName,
                buildCodeBlock {
                    parameters.forEachIndexed { index, parameter ->
                        val valueName = "_ktConfigValue$index"
                        if (parameter.isNullable) {
                            addStatement("%N,", valueName)
                        } else {
                            addStatement("%N!!,", valueName)
                        }
                    }
                },
            )
        }

        private fun FunSpec.Builder.addAggregatingDecodeCode(
            target: LoaderTarget,
            parameters: List<Parameter>,
            defaultValueName: String,
        ) {
            addStatement("val _ktConfigErrors = mutableListOf<%T>()", ktConfigErrorClassName)
            parameters.forEachIndexed { index, parameter ->
                val valueName = "_ktConfigValue$index"
                val valueTypeName = parameter.serializer.typeName.copy(nullable = true)
                if (target.ktConfig.hasDefault) {
                    addStatement("var %N: %T = %N.%N", valueName, valueTypeName, defaultValueName, parameter.name)
                } else {
                    addStatement("var %N: %T = null", valueName, valueTypeName)
                }
                addControlFlowCode("try") {
                    when {
                        target.ktConfig.hasDefault -> {
                            addStatement(
                                "value[%S]?.let(${parameter.serializer.refKey}::deserialize)?.let { %N = it }",
                                parameter.pathName,
                                parameter.serializer.ref,
                                valueName,
                            )
                        }

                        parameter.isNullable -> {
                            addStatement(
                                "%N = value[%S]?.let(${parameter.serializer.refKey}::deserialize)",
                                valueName,
                                parameter.pathName,
                                parameter.serializer.ref,
                            )
                        }

                        else -> {
                            addStatement(
                                "%N = value[%S]?.let(${parameter.serializer.refKey}::deserialize) ?: throw %T(%S)",
                                valueName,
                                parameter.pathName,
                                parameter.serializer.ref,
                                notFoundValueExceptionClassName,
                                parameter.pathName,
                            )
                        }
                    }
                }
                addControlFlowCode("catch (e: %T)", Throwable::class) {
                    addStatement("_ktConfigErrors += %T.fromException(%S, e)", ktConfigErrorClassName, parameter.pathName)
                }
            }
            addControlFlowCode("if (_ktConfigErrors.isNotEmpty())") {
                addStatement("throw %T(_ktConfigErrors)", ktConfigLoadExceptionClassName)
            }
            addCode(
                "return validateLoadedConfig(%T(\n%L))",
                target.typeName,
                buildCodeBlock {
                    parameters.forEachIndexed { index, parameter ->
                        val valueName = "_ktConfigValue$index"
                        if (parameter.isNullable) {
                            addStatement("%N,", valueName)
                        } else {
                            addStatement("%N!!,", valueName)
                        }
                    }
                },
            )
        }

        private fun CodeBlock.Builder.addGeneratedSaveStatements(
            target: LoaderTarget,
            parameters: List<Parameter>,
            valueCode: CodeBlock,
        ) {
            parameters.forEach { parameter ->
                addStatement(
                    "${parameter.serializer.refKey}.set(configuration, \"%L%L\", (%L).%N)",
                    parameter.serializer.ref,
                    $$"${parentPath}",
                    parameter.pathName,
                    valueCode,
                    parameter.name,
                )

                val comment = parameter.comment
                if (comment != null) {
                    addStatement(
                        "setComment(configuration, \"%L%L\", %L)",
                        $$"${parentPath}",
                        parameter.pathName,
                        comment.asLiteralList(),
                    )
                }
            }
        }

        private fun CodeBlock.Builder.addGeneratedDecodeExpression(
            target: LoaderTarget,
            parameters: List<Parameter>,
        ) {
            add("run {\n")
            indent()
            addStatement("val _ktConfigErrors = mutableListOf<%T>()", ktConfigErrorClassName)
            parameters.forEachIndexed { index, parameter ->
                val valueName = "_ktConfigValue$index"
                val valueTypeName = parameter.serializer.typeName.copy(nullable = true)
                if (target.ktConfig.hasDefault) {
                    addStatement(
                        "var %N: %T = %N.%N",
                        valueName,
                        valueTypeName,
                        target.defaultValuePropertyName(),
                        parameter.name,
                    )
                } else {
                    addStatement("var %N: %T = null", valueName, valueTypeName)
                }
                addControlFlowCode("try") {
                    when {
                        target.ktConfig.hasDefault -> {
                            addStatement(
                                "value[%S]?.let(${parameter.serializer.refKey}::deserialize)?.let { %N = it }",
                                parameter.pathName,
                                parameter.serializer.ref,
                                valueName,
                            )
                        }

                        parameter.isNullable -> {
                            addStatement(
                                "%N = value[%S]?.let(${parameter.serializer.refKey}::deserialize)",
                                valueName,
                                parameter.pathName,
                                parameter.serializer.ref,
                            )
                        }

                        else -> {
                            addStatement(
                                "%N = value[%S]?.let(${parameter.serializer.refKey}::deserialize) ?: throw %T(%S)",
                                valueName,
                                parameter.pathName,
                                parameter.serializer.ref,
                                notFoundValueExceptionClassName,
                                parameter.pathName,
                            )
                        }
                    }
                }
                addControlFlowCode("catch (e: %T)", Throwable::class) {
                    addStatement("_ktConfigErrors += %T.fromException(%S, e)", ktConfigErrorClassName, parameter.pathName)
                }
            }
            addControlFlowCode("if (_ktConfigErrors.isNotEmpty())") {
                addStatement("throw %T(_ktConfigErrors)", ktConfigLoadExceptionClassName)
            }
            add(
                "validateLoadedConfig(%T(\n%L))",
                target.typeName,
                buildCodeBlock {
                    parameters.forEachIndexed { index, parameter ->
                        val valueName = "_ktConfigValue$index"
                        if (parameter.isNullable) {
                            addStatement("%N,", valueName)
                        } else {
                            addStatement("%N!!,", valueName)
                        }
                    }
                },
            )
            unindent()
            add("\n}")
        }

        private fun CodeBlock.Builder.addGeneratedEncodeExpression(
            discriminatorKey: String,
            discriminator: String,
            parameters: List<Parameter>,
            valueCode: CodeBlock,
        ) {
            add(
                "mapOf(\n%L)",
                buildCodeBlock {
                    addStatement("%S to %T.serialize(%S),", discriminatorKey, stringSerializerClassName, discriminator)
                    parameters.forEach { parameter ->
                        if (parameter.isNullable) {
                            addStatement(
                                "%S to (%L).%N?.let(${parameter.serializer.refKey}::serialize),",
                                parameter.pathName,
                                valueCode,
                                parameter.name,
                                parameter.serializer.ref,
                            )
                        } else {
                            addStatement(
                                "%S to ${parameter.serializer.refKey}.serialize((%L).%N),",
                                parameter.pathName,
                                parameter.serializer.ref,
                                valueCode,
                                parameter.name,
                            )
                        }
                    }
                },
            )
        }

        private fun CodeBlock.Builder.addSealedSubclassControlFlow(
            subclass: SealedSubclassTarget,
            block: CodeBlock.Builder.() -> Unit,
        ) {
            val typeParameterCount = subclass.declaration.typeParameters.size
            if (typeParameterCount == 0) {
                addControlFlowCode("is %T ->", subclass.checkTypeName, block = block)
            } else {
                addControlFlowCode("is %T<${List(typeParameterCount) { "*" }.joinToString(", ")}> ->", subclass.checkTypeName, block = block)
            }
        }

        private fun CodeBlock.Builder.addSealedSubclassLoaderStatement(
            subclass: SealedSubclassTarget,
            statement: String,
        ) {
            addStatement("%T.$statement", subclass.loaderTypeName, subclass.valueCode())
        }

        private fun SealedSubclassTarget.valueCode() =
            buildCodeBlock {
                if (requiresValueCast) {
                    add("value as %T", valueTypeName)
                } else {
                    add("value")
            }
        }

        private fun LoaderTarget.defaultValuePropertyName() = "${loaderSimpleName}DefaultValue"

        /**
         * Adds a load function to the TypeSpec builder by creating and adding the function specification.
         * This is a convenience wrapper around [createLoadFunSpec].
         *
         * @param className The fully qualified class name to return from the load function
         * @param block Additional configuration for the function builder
         * @return This TypeSpec.Builder for chaining
         */
        private fun TypeSpec.Builder.addLoadFunSpec(
            className: TypeName,
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
            className: TypeName,
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
            className: TypeName,
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
            className: TypeName,
            block: FunSpec.Builder.() -> Unit,
        ) = FunSpec
            .builder("save")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("configuration", yamlConfigurationClassName))
            .addParameter(ParameterSpec("value", className))
            .addParameter(ParameterSpec("parentPath", stringClassName))
            .apply {
                // Get header comment
                val headerComment = classDeclaration.getCommentAnnotation()?.content

                if (headerComment.isNullOrEmpty().not()) {
                    // Add header comment
                    addStatement(
                        "setHeaderComment(configuration, parentPath, %L)",
                        headerComment.asLiteralList(),
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
            className: TypeName,
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
            className: TypeName,
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
            className: TypeName,
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
            className: TypeName,
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
         * @param addedInitializableSerializerNames Serializer property names already added to this generated file
         */
        private fun FileSpec.Builder.addInitializableSerializerProperties(
            parameters: List<Parameter>,
            addedInitializableSerializerNames: MutableSet<String>,
        ) {
            parameters
                .map(Parameter::serializer)
                .extractInitializableSerializers()
                .filter { addedInitializableSerializerNames.add(it.uniqueName) }
                .forEach { serializer ->
                    val className = if (serializer.keyable) keyableSerializerClassName else serializerClassName
                    addProperty(
                        PropertySpec
                            .builder(serializer.uniqueName, className.parameterizedBy(serializer.typeName))
                            .addModifiers(KModifier.PRIVATE)
                            .initializer("%L", serializer.initialize)
                            .build(),
                    )
                }
        }

        private fun getParameters(
            declaration: KSClassDeclaration,
            typeSubstitutions: Map<String, KSType> = emptyMap(),
        ): List<Parameter>? {
            // Get primary constructor from data class
            val primaryConstructor = declaration.primaryConstructor
            if (primaryConstructor == null) {
                logger.error("Classes annotated with @KtConfig must have a primary constructor", declaration)
                return null
            }

            // Get parameters from data class constructor
            return primaryConstructor.parameters.map { createParameter(it, typeSubstitutions) ?: return null }
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
            val serialName = declaration.getSerialNameAnnotation()
            if (serialName != null) {
                return serialName.name
            }

            val qualifiedName = declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Class declaration must have a qualified name", declaration)
                return null
            }
            return qualifiedName
        }

        private fun KSClassDeclaration.toSealedSubclassTargets(target: LoaderTarget): List<SealedSubclassTarget> {
            val typeAliasTargets =
                resolver
                    .getSymbolsWithAnnotation(KT_CONFIG)
                    .filterIsInstance<KSTypeAlias>()
                    .mapNotNull { typeAlias ->
                        val resolvedType = typeAlias.type.resolve()
                        if (resolvedType.declaration != this) {
                            return@mapNotNull null
                        }

                        val typeSubstitutions = typeParameters.toTypeSubstitutions(resolvedType.arguments)
                        if (!isCompatibleSealedSubclass(target, typeSubstitutions)) {
                            return@mapNotNull null
                        }

                        val loaderName = getLoaderName(typeAlias) ?: return@mapNotNull null
                        val discriminator = getDiscriminator(this) ?: return@mapNotNull null
                        val aliasTypeName = ClassName(typeAlias.packageName.asString(), typeAlias.simpleName.asString())
                        val typeAliasKtConfig = typeAlias.getKtConfigAnnotation() ?: return@mapNotNull null
                        val childKtConfig = getKtConfigAnnotation()
                        SealedSubclassTarget(
                            declaration = this,
                            checkTypeName = ClassName(packageName.asString(), getFullName(this)),
                            valueTypeName = aliasTypeName,
                            loaderTypeName = ClassName(typeAlias.packageName.asString(), loaderName),
                            discriminator = discriminator,
                            requiresValueCast = typeParameters.isNotEmpty(),
                            generatedLoaderTarget =
                                LoaderTarget(
                                    declaration = this,
                                    packageName = target.packageName,
                                    typeName = aliasTypeName,
                                    loaderSimpleName = loaderName,
                                    file = target.file,
                                    ktConfig = childKtConfig ?: typeAliasKtConfig.copy(hasDefault = false),
                                    typeSubstitutions = typeSubstitutions,
                                    headerCommentDeclaration = this,
                                ),
                        )
                    }.toList()
            if (typeAliasTargets.isNotEmpty()) {
                if (typeAliasTargets.size > 1 && typeParameters.isNotEmpty()) {
                    logger.error(
                        "Cannot generate sealed loader for generic subtype ${qualifiedName?.asString()} because multiple compatible concrete aliases cannot be distinguished at runtime",
                        this,
                    )
                    return emptyList()
                }
                return typeAliasTargets
            }

            val inferredTypeSubstitutions = inferTypeSubstitutions(target) ?: return emptyList()
            if (!validateResolvedTypeParameters(inferredTypeSubstitutions, target)) {
                return emptyList()
            }
            val classTarget =
                if (isCompatibleSealedSubclass(target, inferredTypeSubstitutions)) {
                    val childKtConfig = getKtConfigAnnotation()
                    val discriminator = getDiscriminator(this)
                    if (discriminator == null) {
                        null
                    } else {
                        val className = ClassName(packageName.asString(), getFullName(this))
                        val valueTypeName =
                            if (typeParameters.isEmpty()) {
                                className
                            } else {
                                val typeNames =
                                    typeParameters.map { parameter ->
                                        inferredTypeSubstitutions[parameter.name.asString()]?.toTypeName()
                                            ?: return emptyList()
                                    }
                                className.parameterizedBy(
                                    typeNames,
                                )
                            }
                        val loaderName = target.syntheticSubclassLoaderName(this)
                        val loaderTypeName = ClassName(target.packageName, loaderName)
                        val generatedLoaderTarget =
                            LoaderTarget(
                                declaration = this,
                                packageName = target.packageName,
                                typeName = valueTypeName,
                                loaderSimpleName = loaderName,
                                file = target.file,
                                ktConfig = childKtConfig ?: target.ktConfig,
                                typeSubstitutions = inferredTypeSubstitutions,
                                headerCommentDeclaration = this,
                            )
                        SealedSubclassTarget(
                            declaration = this,
                            checkTypeName = className,
                            valueTypeName = valueTypeName,
                            loaderTypeName = loaderTypeName,
                            discriminator = discriminator,
                            requiresValueCast = typeParameters.isNotEmpty(),
                            generatedLoaderTarget = generatedLoaderTarget,
                        )
                    }
                } else {
                    null
                }

            return listOfNotNull(classTarget)
        }

        private fun KSClassDeclaration.validateResolvedTypeParameters(
            typeSubstitutions: Map<String, KSType>,
            target: LoaderTarget,
        ): Boolean {
            val unresolvedTypeParameters =
                typeParameters
                    .map { it.name.asString() }
                    .filterNot(typeSubstitutions::containsKey)
            if (unresolvedTypeParameters.isEmpty()) {
                return true
            }

            logger.error(
                "Cannot generate sealed loader for generic subtype ${qualifiedName?.asString()} because type parameter(s) ${unresolvedTypeParameters.joinToString()} are not resolved by ${target.typeName}",
                this,
            )
            return false
        }

        private fun LoaderTarget.syntheticSubclassLoaderName(declaration: KSClassDeclaration) =
            buildString {
                append(loaderSimpleName)
                append(getFullName(declaration).joinToString(""))
                append("Loader")
            }

        private fun KSType.toTypeName(): TypeName {
            val qualifiedName = declaration.qualifiedName?.asString()
            val className =
                if (qualifiedName == null) {
                    ClassName(declaration.packageName.asString(), declaration.simpleName.asString())
                } else {
                    ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))
                }
            val typeName =
                if (arguments.isEmpty()) {
                    className
                } else {
                    className.parameterizedBy(
                        arguments.map { argument ->
                            argument.type?.resolve()?.toTypeName() ?: STAR
                        },
                    )
                }
            return typeName.copy(nullable = isMarkedNullable)
        }

        private val STAR = com.squareup.kotlinpoet.STAR

        private fun KSClassDeclaration.inferTypeSubstitutions(target: LoaderTarget): Map<String, KSType>? {
            if (typeParameters.isEmpty()) {
                return emptyMap()
            }

            val targetQualifiedName = target.declaration.qualifiedName?.asString() ?: return null
            val superType =
                getAllSuperTypes().firstOrNull {
                    it.declaration.qualifiedName?.asString() == targetQualifiedName
                } ?: return null

            val substitutions = mutableMapOf<String, KSType>()
            target.declaration.typeParameters.zip(superType.arguments).forEach { (parameter, argument) ->
                val expectedType = target.typeSubstitutions[parameter.name.asString()] ?: return@forEach
                val patternType = argument.type?.resolve() ?: return null
                if (!inferTypeSubstitution(patternType, expectedType, substitutions)) {
                    return null
                }
            }
            return substitutions
        }

        private fun KSClassDeclaration.inferTypeSubstitution(
            patternType: KSType,
            expectedType: KSType,
            substitutions: MutableMap<String, KSType>,
        ): Boolean {
            val typeParameter = patternType.declaration as? KSTypeParameter
            if (typeParameter != null && typeParameters.any { it.name.asString() == typeParameter.name.asString() }) {
                if (patternType.isMarkedNullable && !expectedType.isMarkedNullable) {
                    logger.error(
                        "Sealed subtype ${qualifiedName?.asString()} is not compatible with the requested parent type because nullable type parameter ${typeParameter.name.asString()}? cannot match non-null type ${expectedType.toTypeName()}",
                        this,
                    )
                    return false
                }
                val substitution =
                    if (patternType.isMarkedNullable && expectedType.isMarkedNullable) {
                        expectedType.makeNotNullable()
                    } else {
                        expectedType
                    }
                val name = typeParameter.name.asString()
                val previous = substitutions[name]
                if (previous != null) {
                    return previous.isSameType(substitution)
                }
                substitutions[name] = substitution
                return true
            }

            if (patternType.isMarkedNullable != expectedType.isMarkedNullable) {
                return false
            }
            if (patternType.declaration.qualifiedName?.asString() != expectedType.declaration.qualifiedName?.asString()) {
                return false
            }
            if (patternType.arguments.size != expectedType.arguments.size) {
                return false
            }

            return patternType.arguments.zip(expectedType.arguments).all { (patternArgument, expectedArgument) ->
                val patternArgumentType = patternArgument.type?.resolve()
                val expectedArgumentType = expectedArgument.type?.resolve()
                when {
                    patternArgumentType == null && expectedArgumentType == null -> true
                    patternArgumentType == null || expectedArgumentType == null -> false
                    else -> inferTypeSubstitution(patternArgumentType, expectedArgumentType, substitutions)
                }
            }
        }

        private fun KSClassDeclaration.isCompatibleSealedSubclass(
            target: LoaderTarget,
            subclassTypeSubstitutions: Map<String, KSType>,
        ): Boolean {
            if (target.typeSubstitutions.isEmpty()) {
                return true
            }

            val targetQualifiedName = target.declaration.qualifiedName?.asString() ?: return true
            val superType =
                getAllSuperTypes().firstOrNull {
                    it.declaration.qualifiedName?.asString() == targetQualifiedName
                } ?: return true

            return target.declaration.typeParameters.zip(superType.arguments).all { (parameter, argument) ->
                val expectedType = target.typeSubstitutions[parameter.name.asString()] ?: return@all true
                val argumentType = argument.type?.resolve() ?: return@all false
                val actualType = substitute(argumentType, subclassTypeSubstitutions)
                actualType.isSameType(expectedType)
            }
        }

        private fun KSType.isSameType(other: KSType): Boolean {
            if (isMarkedNullable != other.isMarkedNullable) {
                return false
            }
            if (declaration.qualifiedName?.asString() != other.declaration.qualifiedName?.asString()) {
                return false
            }
            if (arguments.size != other.arguments.size) {
                return false
            }
            return arguments.zip(other.arguments).all { (actual, expected) ->
                val actualType = actual.type?.resolve()
                val expectedType = expected.type?.resolve()
                when {
                    actualType == null && expectedType == null -> true
                    actualType == null || expectedType == null -> false
                    else -> actualType.isSameType(expectedType)
                }
            }
        }

        /**
         * Creates a Parameter object from a KSValueParameter, validating the parameter name and type.
         * Returns null if the parameter is invalid or unsupported.
         */
        private fun createParameter(
            declaration: KSValueParameter,
            typeSubstitutions: Map<String, KSType> = emptyMap(),
        ): Parameter? {
            val name = declaration.name?.asString()
            if (name == null) {
                logger.error("Primary constructor parameters must have a name", declaration)
                return null
            }

            val serializer = getSerializer(substitute(declaration.type.resolve(), typeSubstitutions)) ?: return null
            val pathName = declaration.getSerialNameAnnotation()?.name
            val comment = declaration.getCommentAnnotation()?.content
            return Parameter(pathName ?: name, name, serializer, comment)
        }

        private fun List<KSTypeParameter>.toTypeSubstitutions(arguments: List<KSTypeArgument>): Map<String, KSType> =
            zip(arguments)
                .mapNotNull { (parameter, argument) ->
                    val type = argument.type?.resolve() ?: return@mapNotNull null
                    parameter.name.asString() to type
                }.toMap()

        private fun substitute(
            type: KSType,
            substitutions: Map<String, KSType>,
        ): KSType {
            val typeParameter = type.declaration as? KSTypeParameter
            if (typeParameter != null) {
                val substituted = substitutions[typeParameter.name.asString()] ?: return type
                return if (type.isMarkedNullable) substituted.makeNullable() else substituted.makeNotNullable()
            }

            if (type.arguments.isEmpty()) {
                return type
            }

            return type.replace(
                type.arguments.map { argument ->
                    val argumentType = argument.type?.resolve()
                    if (argumentType == null) {
                        argument
                    } else {
                        resolver.getTypeArgument(
                            resolver.createKSTypeReferenceFromKSType(substitute(argumentType, substitutions)),
                            if (argument.variance == Variance.STAR) Variance.INVARIANT else argument.variance,
                        )
                    }
                },
            )
        }

        private fun KSType.solveTypeAlias(): Pair<KSType, Serializer.Custom?> {
            val declaration = this.declaration
            val annotation =
                // Get typealias-annotated serializer
                getUseSerializerAnnotation()
                    ?: // Get class-annotated serializer
                    declaration.getUseSerializerAnnotation()
            val serializer =
                annotation
                    ?.serializer
                    ?.declaration
                    ?.qualifiedName
                    ?.asString()
                    ?.let(Serializer::Custom)

            // Solve typealias
            if (declaration is KSTypeAlias) {
                val (resolvedType, resolvedSerializer) = declaration.type.resolve().solveTypeAlias()
                return resolvedType.let {
                    if (isMarkedNullable) {
                        it.makeNullable()
                    } else {
                        it.makeNotNullable()
                    }
                } to (resolvedSerializer ?: serializer)
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
            val className =
                if (declaration is KSClassDeclaration) {
                    ClassName(declaration.packageName.asString(), getFullName(declaration))
                } else {
                    ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))
                }

            // Handle enum class, value class
            val modifiers = declaration.modifiers
            when {
                modifiers.contains(Modifier.ENUM) -> {
                    return Parameter.Serializer.EnumClass(className, type.isMarkedNullable)
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
            val serializer = customSerializer ?: Serializer.findSerializer(qualifiedName, type)
            if (serializer == null) {
                logger.error("Unsupported type: $qualifiedName", declaration)
                return null
            }

            // Check for generic type arguments and create Class serializer if present
            val isNullable = type.isMarkedNullable

            when (serializer) {
                Serializer.ConfigurationSerializable -> {
                    return Parameter.Serializer.ConfigurationSerializableClass(className, isNullable)
                }

                is Serializer.BuiltIn -> {
                    return Parameter.Serializer.Object(className, isNullable, serializer.name, serializer.serializerType)
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
                            serializer.serializerType,
                            argumentSerializers,
                            serializer.supportNullableValue && nullableValue,
                        )
                    }

                    return Parameter.Serializer.Object(className, isNullable, serializer.name, serializer.serializerType)
                }

                is Serializer.Nested -> {
                    return Parameter.Serializer.Nested(
                        className,
                        isNullable,
                        serializer.qualifiedName,
                        serializer.loaderType,
                    )
                }

                is Serializer.Custom -> {
                    return Parameter.Serializer.Object(
                        className,
                        isNullable,
                        serializer.qualifiedName.replace('.', '_'), // unique name
                        serializer.serializerType,
                    )
                }
            }
        }
    }
}
