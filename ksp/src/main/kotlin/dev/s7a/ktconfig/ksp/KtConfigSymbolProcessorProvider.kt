package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Creates [KtConfigSymbolProcessor] instances for the configured platform.
 *
 * Since 2.3.0, the `ktconfig.platform` KSP option accepts `bukkit` (the default) or `fabric`.
 *
 * @since 2.0.0
 */
class KtConfigSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val platform = environment.options["ktconfig.platform"] ?: "bukkit"
        if (platform !in setOf("bukkit", "fabric")) {
            environment.logger.error("ktconfig.platform must be either 'bukkit' or 'fabric', but was '$platform'")
        }
        return KtConfigSymbolProcessor(environment.codeGenerator, environment.logger, platform)
    }
}
