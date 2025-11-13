package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
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
 * Symbol processor that generates loader classes for configurations annotated with @ForKtConfig.
 * This processor handles the code generation for configuration classes by creating corresponding loader implementations.
 */
class KtConfigSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    companion object {
        private const val FOR_KT_CONFIG = "dev.s7a.ktconfig.ForKtConfig"
    }

    /**
     * Processes all classes annotated with @ForKtConfig and generates their corresponding loader classes.
     * @param resolver The symbol resolver to find annotated classes
     * @return Empty list as all symbols are processed in a single round
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(FOR_KT_CONFIG)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { it.accept(Visitor(), Unit) }
        return emptyList()
    }

    private inner class Visitor : KSVisitorVoid() {
        private val loaderClassName = ClassName("dev.s7a.ktconfig", "KtConfigLoader")
        private val serializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer")
        private val keyableSerializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer.Keyable")
        private val yamlConfigurationClassName = ClassName("org.bukkit.configuration.file", "YamlConfiguration")

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
                logger.error("Classes annotated with @ForKtConfig must have a primary constructor", classDeclaration)
                return
            }

            // Get parameters from data class constructor
            val parameters = primaryConstructor.parameters.map { createParameter(it) ?: return }

            val packageName = classDeclaration.packageName.asString()
            val simpleName = classDeclaration.simpleName.asString()
            val className = ClassName(packageName, simpleName)
            val loaderSimpleName = "${simpleName}Loader"

            FileSpec
                .builder(packageName, loaderSimpleName)
                .apply {
                    addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "ktlint").build())

                    // Add properties for nested type serializer classes like ListOfString
                    parameters
                        .map(Parameter::serializer)
                        .extractInitializableSerializers()
                        .forEach {
                            addProperty(
                                PropertySpec
                                    .builder(
                                        it.uniqueName,
                                        (if (it.keyable) keyableSerializerClassName else serializerClassName).parameterizedBy(it.type),
                                    ).addModifiers(KModifier.PRIVATE)
                                    .initializer("%L", it.initialize)
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
                                    .addCode(
                                        "return %T(\n",
                                        className,
                                    ).apply {
                                        parameters.forEach { parameter ->
                                            addStatement(
                                                "%L.%N(configuration, %S),",
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
                                    .apply {
                                        parameters.forEach {
                                            addStatement(
                                                "%L.%N(configuration, %S, value.%N)",
                                                it.serializer.ref,
                                                "set",
                                                it.pathName,
                                                it.name,
                                            )
                                        }
                                    }.build(),
                            ).build(),
                    )
                }.build()
                .writeTo(codeGenerator, false)
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

            return Parameter(name, name, serializer)
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
                // Collection
                "kotlin.ByteArray" to "ByteArray",
                "kotlin.CharArray" to "CharArray",
                "kotlin.IntArray" to "IntArray",
                "kotlin.LongArray" to "LongArray",
                "kotlin.ShortArray" to "ShortArray",
                "kotlin.UByteArray" to "UByteArray",
                "kotlin.UIntArray" to "UIntArray",
                "kotlin.ULongArray" to "ULongArray",
                "kotlin.UShortArray" to "UShortArray",
                "kotlin.DoubleArray" to "DoubleArray",
                "kotlin.FloatArray" to "FloatArray",
                "kotlin.BooleanArray" to "BooleanArray",
                "kotlin.collections.List" to "List",
                "kotlin.collections.Set" to "Set",
                "kotlin.collections.ArrayDeque" to "ArrayDeque",
                "kotlin.collections.Map" to "Map",
            )

        private fun getSerializer(declaration: KSValueParameter): Parameter.Serializer? {
            val type = declaration.type.resolve()
            val qualifiedName = type.declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Type must have a qualified name", declaration)
                return null
            }

            return getSerializer(declaration, type, qualifiedName)
        }

        private fun getSerializer(declaration: KSTypeArgument): Parameter.Serializer? {
            val type = declaration.type?.resolve()
            val qualifiedName = type?.declaration?.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Type must have a qualified name", declaration)
                return null
            }

            return getSerializer(declaration, type, qualifiedName)
        }

        /**
         * Resolves the appropriate serializer for a given parameter type.
         * Handles both simple types and generic collections, returning null for unsupported types.
         */
        private fun getSerializer(
            declaration: KSAnnotated,
            type: KSType,
            qualifiedName: String,
        ): Parameter.Serializer? {
            val className = ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))

            val modifiers = type.declaration.modifiers
            when {
                modifiers.contains(Modifier.ENUM) -> {
                    return Parameter.Serializer.EnumClass(className, type.isMarkedNullable, qualifiedName)
                }
                modifiers.contains(Modifier.VALUE) -> {
                    val classDeclaration = type.declaration as KSClassDeclaration

                    val primaryConstructor = classDeclaration.primaryConstructor
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

            // Lookup serializer name from the predefined map of built-in serializers
            val serializer = builtInSerializers[qualifiedName]
            if (serializer == null) {
                logger.error("Unsupported type: $qualifiedName", declaration)
                return null
            }

            // Check for generic type arguments and create Class serializer if present
            val isNullable = type.isMarkedNullable
            val arguments = type.arguments
            if (arguments.isNotEmpty()) {
                val argumentSerializers =
                    arguments.map { argument ->
                        getSerializer(argument) ?: return null
                    }

                return Parameter.Serializer.Class(className, isNullable, serializer, argumentSerializers)
            }

            return Parameter.Serializer.Object(className, isNullable, serializer)
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
    ) {
        sealed class Serializer(
            val type: TypeName,
            isNullable: Boolean,
            name: String,
        ) {
            protected val classRef = "dev.s7a.ktconfig.serializer.${name}Serializer"

            abstract val uniqueName: String
            abstract val ref: String
            abstract val keyable: Boolean

            val getFn = if (isNullable) "get" else "getOrThrow"

            class Object(
                type: ClassName,
                isNullable: Boolean,
                name: String,
            ) : Serializer(type, isNullable, name) {
                override val uniqueName = name
                override val ref = classRef
                override val keyable = true
            }

            sealed class InitializableSerializer(
                type: TypeName,
                isNullable: Boolean,
                name: String,
            ) : Serializer(type, isNullable, name) {
                abstract val initialize: String
            }

            // Properties like type, uniqueName, ref are stored as class properties
            // to avoid recalculating them each time they are accessed
            class Class(
                parentType: ClassName,
                isNullable: Boolean,
                name: String,
                val arguments: List<Serializer>,
            ) : InitializableSerializer(parentType.parameterizedBy(arguments.map { it.type }), isNullable, name) {
                override val uniqueName =
                    buildString {
                        append(name)
                        append("Of")
                        arguments.forEach {
                            append(it.uniqueName)
                        }
                    }
                override val ref = uniqueName
                override val keyable = false
                override val initialize = "$classRef(${arguments.joinToString(", ") { it.ref }})"
            }

            class EnumClass(
                type: ClassName,
                isNullable: Boolean,
                qualifiedName: String,
            ) : InitializableSerializer(type, isNullable, "Enum") {
                override val uniqueName = type.canonicalName.replace(".", "_")
                override val ref = uniqueName
                override val keyable = true
                override val initialize = "$classRef($qualifiedName::class.java)"
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
                        append(classRef)
                        if (keyable) append(".Keyable")
                        append("(${argument.ref}, { $type(it) }, { it.$parameterName })")
                    }
            }
        }
    }
}
