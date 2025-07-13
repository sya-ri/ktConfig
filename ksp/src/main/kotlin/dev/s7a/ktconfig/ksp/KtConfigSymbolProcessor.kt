package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
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

    private fun KSClassDeclaration.isDataClass() = modifiers.contains(Modifier.DATA)

    private inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            if (classDeclaration.isDataClass().not()) {
                logger.error("Classes annotated with @ForKtConfig must be data classes", classDeclaration)
                return
            }

            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()
            val loaderClassName = "${className}Loader"

            logger.info("Generate $packageName.$loaderClassName")

            FileSpec
                .builder(packageName, loaderClassName)
                .apply {
                    addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "ktlint").build())

                    addFunction(FunSpec.builder("load$className").addStatement("println(%S)", "TODO: load$className").build())
                    addFunction(FunSpec.builder("save$className").addStatement("println(%S)", "TODO: save$className").build())
                }.build()
                .writeTo(codeGenerator, false)
        }
    }
}
