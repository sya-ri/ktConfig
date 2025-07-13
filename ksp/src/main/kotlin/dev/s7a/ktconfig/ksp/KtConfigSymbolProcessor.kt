package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated

class KtConfigSymbolProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    companion object {
        private const val FOR_KT_CONFIG = "dev.s7a.ktconfig.ForKtConfig"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val configs = resolver.getSymbolsWithAnnotation(FOR_KT_CONFIG)

        configs.forEach {
            println("Found config: $it")
        }

        return emptyList()
    }
}
