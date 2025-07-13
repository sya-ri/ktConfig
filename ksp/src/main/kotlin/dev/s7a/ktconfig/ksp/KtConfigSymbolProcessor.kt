package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
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
        private val primitiveSerializers =
            mapOf(
                "kotlin.Byte" to Parameter.Serializer("ByteSerializer"),
                "kotlin.Char" to Parameter.Serializer("CharSerializer"),
                "kotlin.Double" to Parameter.Serializer("DoubleSerializer"),
                "kotlin.Float" to Parameter.Serializer("FloatSerializer"),
                "kotlin.Int" to Parameter.Serializer("IntSerializer"),
                "kotlin.Long" to Parameter.Serializer("LongSerializer"),
                "kotlin.Number" to Parameter.Serializer("NumberSerializer"),
                "kotlin.Short" to Parameter.Serializer("ShortSerializer"),
                "kotlin.String" to Parameter.Serializer("StringSerializer"),
                "kotlin.UByte" to Parameter.Serializer("UByteSerializer"),
                "kotlin.UInt" to Parameter.Serializer("UIntSerializer"),
                "kotlin.ULong" to Parameter.Serializer("ULongSerializer"),
                "kotlin.UShort" to Parameter.Serializer("UShortSerializer"),
            )

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
            val parameters =
                primaryConstructor.parameters.map {
                    val name = it.name?.asString()
                    if (name == null) {
                        logger.error("Primary constructor parameters must have a name", it)
                        return
                    }

                    val type = it.type.resolve()
                    val className = type.declaration.qualifiedName?.asString()
                    if (className == null) {
                        logger.error("Primary constructor parameters must be a class", it)
                    }

                    val serializer = primitiveSerializers[className]
                    if (serializer == null) {
                        logger.error("Unsupported type: $className", it)
                        return
                    }

                    Parameter(name, name, serializer)
                }

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
                                                "%M(configuration, %S),",
                                                parameter.serializer.getOrThrowFun,
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
                                                "%M(configuration, %S, value.%N)",
                                                it.serializer.saveFun,
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
    }

    private data class Parameter(
        val pathName: String,
        val name: String,
        val serializer: Serializer,
    ) {
        data class Serializer(
            val name: String,
        ) {
            val className = ClassName("dev.s7a.ktconfig.serializer", name)
            val getOrThrowFun = MemberName(className, "getOrThrow")
            val saveFun = MemberName(className, "save")
        }
    }
}
