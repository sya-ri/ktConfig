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
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KtConfigSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    companion object {
        private const val FOR_KT_CONFIG = "dev.s7a.ktconfig.ForKtConfig"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(FOR_KT_CONFIG)
        symbols
            .filter { it is KSClassDeclaration }
            .forEach { it.accept(Visitor(), Unit) }
        return emptyList()
    }

    private inner class Visitor : KSVisitorVoid() {
        private val ktConfigLoaderClassName = ClassName("dev.s7a.ktconfig", "KtConfigLoader")
        private val yamlConfigurationClassName = ClassName("org.bukkit.configuration.file", "YamlConfiguration")

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

                    addType(
                        TypeSpec
                            .objectBuilder(loaderSimpleName)
                            .superclass(ktConfigLoaderClassName.parameterizedBy(className))
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
                                                parameter.serializer.init,
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
                                                it.serializer.init,
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

        private val serializers =
            mapOf(
                // Primitive
                "kotlin.Byte" to "ByteSerializer",
                "kotlin.Char" to "CharSerializer",
                "kotlin.Double" to "DoubleSerializer",
                "kotlin.Float" to "FloatSerializer",
                "kotlin.Int" to "IntSerializer",
                "kotlin.Long" to "LongSerializer",
                "kotlin.Number" to "NumberSerializer",
                "kotlin.Short" to "ShortSerializer",
                "kotlin.String" to "StringSerializer",
                "kotlin.UByte" to "UByteSerializer",
                "kotlin.UInt" to "UIntSerializer",
                "kotlin.ULong" to "ULongSerializer",
                "kotlin.UShort" to "UShortSerializer",
                // Collection
                "kotlin.collections.List" to "ListSerializer",
            )

        private fun createParameter(declaration: KSValueParameter): Parameter? {
            val name = declaration.name?.asString()
            if (name == null) {
                logger.error("Primary constructor parameters must have a name", declaration)
                return null
            }

            val type = declaration.type.resolve()
            val className = type.declaration.qualifiedName?.asString()
            if (className == null) {
                logger.error("Primary constructor parameters must be a class", declaration)
                return null
            }

            val serializer = getSerializer(declaration, type, className) ?: return null

            return Parameter(name, name, serializer)
        }

        private fun getSerializer(
            declaration: KSValueParameter,
            type: KSType,
            className: String,
        ): Parameter.Serializer? {
            val serializer = serializers[className]
            if (serializer == null) {
                logger.error("Unsupported type: $className", declaration)
                return null
            }

            val arguments = type.arguments
            if (arguments.isNotEmpty()) {
                val argumentSerializers =
                    arguments.map { argument ->
                        val argumentType = argument.type?.resolve()
                        val argumentClassName =
                            argumentType
                                ?.declaration
                                ?.qualifiedName
                                ?.asString()
                        if (argumentClassName == null) {
                            logger.error("Type arguments must be a class", argument)
                            return null
                        }

                        getSerializer(declaration, argumentType, argumentClassName) ?: return null
                    }

                return Parameter.Serializer.Class(serializer, argumentSerializers)
            }

            return Parameter.Serializer.Object(serializer)
        }
    }

    private data class Parameter(
        val pathName: String,
        val name: String,
        val serializer: Serializer,
    ) {
        sealed interface Serializer {
            val init: String

            data class Object(
                val name: String,
            ) : Serializer {
                override val init = "dev.s7a.ktconfig.serializer.$name"
            }

            data class Class(
                val name: String,
                val arguments: List<Serializer>,
            ) : Serializer {
                override val init = "dev.s7a.ktconfig.serializer.$name(${arguments.joinToString(", ") { it.init }})"
            }
        }
    }
}
