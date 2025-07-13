package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
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
        val symbols = resolver.getSymbolsWithAnnotation(FOR_KT_CONFIG)
        symbols
            .filter { it is KSClassDeclaration }
            .forEach { it.accept(Visitor(), Unit) }
        return emptyList()
    }

    private inner class Visitor : KSVisitorVoid() {
        private val loaderClassName = ClassName("dev.s7a.ktconfig", "KtConfigLoader")
        private val serializerClassName = ClassName("dev.s7a.ktconfig.serializer", "Serializer")
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
                        .distinct()
                        .filterIsInstance<Parameter.Serializer.Class>()
                        .forEach {
                            addProperty(
                                PropertySpec
                                    .builder(it.uniqueName, serializerClassName.parameterizedBy(it.type))
                                    .addModifiers(KModifier.PRIVATE)
                                    .initializer("%L", it.init)
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
                                                "getOrThrow",
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
                                                "save",
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

            val type = declaration.type.resolve()
            val qualifiedName = type.declaration.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("Primary constructor parameters must be a class", declaration)
                return null
            }

            val serializer = getSerializer(declaration, type, qualifiedName) ?: return null

            return Parameter(name, name, serializer)
        }

        private val builtInSerializers =
            mapOf(
                // Primitive
                "kotlin.Byte" to "Byte",
                "kotlin.Char" to "Char",
                "kotlin.Double" to "Double",
                "kotlin.Float" to "Float",
                "kotlin.Int" to "Int",
                "kotlin.Long" to "Long",
                "kotlin.Number" to "Number",
                "kotlin.Short" to "Short",
                "kotlin.String" to "String",
                "kotlin.UByte" to "UByte",
                "kotlin.UInt" to "UInt",
                "kotlin.ULong" to "ULong",
                "kotlin.UShort" to "UShort",
                // Collection
                "kotlin.collections.List" to "List",
            )

        /**
         * Resolves the appropriate serializer for a given parameter type.
         * Handles both simple types and generic collections, returning null for unsupported types.
         */
        private fun getSerializer(
            declaration: KSValueParameter,
            type: KSType,
            qualifiedName: String,
        ): Parameter.Serializer? {
            val serializer = builtInSerializers[qualifiedName]
            if (serializer == null) {
                logger.error("Unsupported type: $qualifiedName", declaration)
                return null
            }

            val className = ClassName(qualifiedName.substringBeforeLast("."), qualifiedName.substringAfterLast("."))

            val arguments = type.arguments
            if (arguments.isNotEmpty()) {
                val argumentSerializers =
                    arguments.map { argument ->
                        val argumentType = argument.type?.resolve()
                        val argumentQualifiedName =
                            argumentType
                                ?.declaration
                                ?.qualifiedName
                                ?.asString()
                        if (argumentQualifiedName == null) {
                            logger.error("Type arguments must be a class", argument)
                            return null
                        }

                        getSerializer(declaration, argumentType, argumentQualifiedName) ?: return null
                    }

                return Parameter.Serializer.Class(className, serializer, argumentSerializers)
            }

            return Parameter.Serializer.Object(className, serializer)
        }
    }

    private data class Parameter(
        val pathName: String,
        val name: String,
        val serializer: Serializer,
    ) {
        sealed interface Serializer {
            val type: TypeName
            val uniqueName: String
            val ref: String

            data class Object(
                override val type: ClassName,
                val name: String,
            ) : Serializer {
                override val uniqueName = name
                override val ref = "dev.s7a.ktconfig.serializer.${name}Serializer"
            }

            // Properties like type, uniqueName, ref are stored as class properties
            // to avoid recalculating them each time they are accessed
            data class Class(
                val parentType: ClassName,
                val name: String,
                val arguments: List<Serializer>,
            ) : Serializer {
                override val type = parentType.parameterizedBy(arguments.map { it.type })

                override val uniqueName =
                    buildString {
                        append(name)
                        append("Of")
                        arguments.forEach {
                            append(it.uniqueName)
                        }
                    }

                override val ref = uniqueName

                val init = "dev.s7a.ktconfig.serializer.${name}Serializer(${arguments.joinToString(", ") { it.ref }})"
            }
        }
    }
}
