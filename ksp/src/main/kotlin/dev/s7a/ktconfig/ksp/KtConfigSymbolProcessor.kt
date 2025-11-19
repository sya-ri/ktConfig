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
import com.squareup.kotlinpoet.ksp.writeTo

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
        private val keyableSerializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer.Keyable")
        private val yamlConfigurationClassName = ClassName("org.bukkit.configuration.file", "YamlConfiguration")
        private val stringClassName = ClassName("kotlin", "String")

        /**
         * Visits each class declaration and generates a corresponding loader class.
         * Processes the class's primary constructor parameters to create load/save implementations.
         */
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            // Get primary constructor from data class
            val primaryConstructor = classDeclaration.primaryConstructor
            if (primaryConstructor == null) {
                logger.error("Classes annotated with @KtConfig must have a primary constructor", classDeclaration)
                return
            }

            // Get header comment
            val headerComment = classDeclaration.annotations.getComment()

            // Get parameters from data class constructor
            val parameters = primaryConstructor.parameters.map { createParameter(it) ?: return }

            val packageName = classDeclaration.packageName.asString()
            val fullName = getFullName(classDeclaration)
            val className = ClassName(packageName, fullName)
            val loaderSimpleName = getLoaderName(classDeclaration)

            FileSpec
                .builder(packageName, loaderSimpleName)
                .apply {
                    addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "ktlint").build())

                    // Add properties for nested type serializer classes like ListOfString
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

                    addType(
                        TypeSpec
                            .objectBuilder(loaderSimpleName)
                            .superclass(loaderClassName.parameterizedBy(className))
                            .addFunction(
                                // Add `load` function to KtConfig loader class
                                FunSpec
                                    .builder("load")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addParameter(ParameterSpec("configuration", yamlConfigurationClassName))
                                    .addParameter(ParameterSpec("parentPath", stringClassName))
                                    .addCode(
                                        "return %T(\n",
                                        className,
                                    ).apply {
                                        parameters.forEach { parameter ->
                                            addStatement(
                                                $$"%L.%N(configuration, \"${parentPath}%L\"),",
                                                parameter.serializer.ref,
                                                parameter.serializer.getFn,
                                                parameter.pathName,
                                            )
                                        }
                                    }.addCode(")")
                                    .returns(className)
                                    .build(),
                            ).addFunction(
                                // Add `save` function to KtConfig loader class
                                FunSpec
                                    .builder("save")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addParameter(ParameterSpec("configuration", yamlConfigurationClassName))
                                    .addParameter(ParameterSpec("value", className))
                                    .addParameter(ParameterSpec("parentPath", stringClassName))
                                    .apply {
                                        if (headerComment != null) {
                                            // Add header comment
                                            addStatement(
                                                "setHeaderComment(configuration, parentPath, listOf(%L))",
                                                headerComment.joinToString { "\"${it}\"" },
                                            )
                                        }

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
                                    }.build(),
                            ).build(),
                    )
                }.build()
                .writeTo(codeGenerator, false)
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
         * Checks if the sequence contains a @KtConfig annotation.
         *
         * @return True if @KtConfig annotation is present, false otherwise
         */
        private fun Sequence<KSAnnotation>.hasKtConfig() = any { it.shortName.asString() == "KtConfig" }

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
            val comment = declaration.annotations.getComment()
            return Parameter(name, name, serializer, comment)
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
            val serializer = annotations.getSerializer()

            val alias = this.declaration
            if (alias is KSTypeAlias) {
                val (resolvedType, resolvedSerializer) = alias.type.resolve().solveTypeAlias()

                // Allow overriding serializer by checking custom serializer first before falling back to resolved serializer
                //
                // typealias IncorrectString =
                //         @UseSerializer(IncorrectSerializer::class)
                //         String
                //
                // typealias OverrideIncorrectString =
                //         @UseSerializer(StringSerializer::class)
                //         IncorrectString
                //
                // OverrideIncorrectString should be serialized with StringSerializer, not IncorrectSerializer
                return resolvedType to (serializer ?: resolvedSerializer)
            }

            return this to serializer
        }

        private fun getSerializer(declaration: KSValueParameter): Parameter.Serializer? {
            val (type, customSerializer) = declaration.type.resolve().solveTypeAlias()
            val qualifiedName = type.declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Type must have a qualified name", declaration)
                return null
            }

            return getSerializer(type, qualifiedName, customSerializer)
        }

        private fun getSerializer(declaration: KSTypeArgument): Parameter.Serializer? {
            val declarationType = declaration.type
            if (declarationType == null) {
                logger.error("Type argument must have a type", declaration)
                return null
            }
            val (type, customSerializer) = declarationType.resolve().solveTypeAlias()
            val qualifiedName = type.declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Type must have a qualified name", declaration)
                return null
            }

            return getSerializer(type, qualifiedName, customSerializer)
        }

        /**
         * Resolves the appropriate serializer for a given parameter type.
         * Handles both simple types and generic collections, returning null for unsupported types.
         */
        private fun getSerializer(
            type: KSType,
            qualifiedName: String,
            customSerializer: Serializer.Custom?,
        ): Parameter.Serializer? {
            val className = ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))

            val declaration = type.declaration
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

        private val builtInSerializers =
            mapOf(
                // Primitive
                "kotlin.Byte" to "Byte",
                "kotlin.Char" to "Char",
                "kotlin.Int" to "Int",
                "kotlin.Long" to "Long",
                "kotlin.Short" to "Short",
                "kotlin.String" to "String",
                "kotlin.UByte" to "UByte",
                "kotlin.UInt" to "UInt",
                "kotlin.ULong" to "ULong",
                "kotlin.UShort" to "UShort",
                "kotlin.Double" to "Double",
                "kotlin.Float" to "Float",
                "kotlin.Boolean" to "Boolean",
                // Common
                "java.util.UUID" to "UUID",
            )

        private val collectionSerializers =
            mapOf(
                "kotlin.Array" to ("Array" to true),
                "kotlin.ByteArray" to ("ByteArray" to false),
                "kotlin.CharArray" to ("CharArray" to false),
                "kotlin.IntArray" to ("IntArray" to false),
                "kotlin.LongArray" to ("LongArray" to false),
                "kotlin.ShortArray" to ("ShortArray" to false),
                "kotlin.UByteArray" to ("UByteArray" to false),
                "kotlin.UIntArray" to ("UIntArray" to false),
                "kotlin.ULongArray" to ("ULongArray" to false),
                "kotlin.UShortArray" to ("UShortArray" to false),
                "kotlin.DoubleArray" to ("DoubleArray" to false),
                "kotlin.FloatArray" to ("FloatArray" to false),
                "kotlin.BooleanArray" to ("BooleanArray" to false),
                "kotlin.collections.List" to ("List" to true),
                "kotlin.collections.Set" to ("Set" to true),
                "kotlin.collections.ArrayDeque" to ("ArrayDeque" to true),
                "kotlin.collections.Map" to ("Map" to true),
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
                if (declaration.annotations.hasKtConfig()) {
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
            val builtIn = builtInSerializers[qualifiedName]
            if (builtIn != null) {
                return Serializer.BuiltIn(builtIn)
            }

            val collection = collectionSerializers[qualifiedName]
            if (collection != null) {
                return Serializer.Collection(collection.first, collection.second)
            }

            return null
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

    private data class Parameter(
        val pathName: String,
        val name: String,
        val serializer: Serializer,
        val comment: List<String>?,
    ) {
        sealed class Serializer(
            val type: TypeName,
            isNullable: Boolean,
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
