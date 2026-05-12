package dev.s7a.ktconfig.exception

import dev.s7a.ktconfig.KtConfigError

/**
 * Exception thrown when config loading collects one or more errors.
 *
 * @property errors All loading errors collected before throwing.
 * @since 2.2.0
 */
class KtConfigLoadException(
    val errors: List<KtConfigError>,
) : KtConfigException(
        buildString {
            append("Failed to load config (")
            append(errors.size)
            append(if (errors.size == 1) " error" else " errors")
            append("):")
            errors.forEach { error ->
                appendLine()
                append("- ")
                if (error.path.isNotEmpty()) {
                    append("[")
                    append(error.path)
                    append("] ")
                }
                append(error.message)
            }
        },
    ) {
    /**
     * Creates an exception for a single aggregated loading error.
     *
     * @since 2.2.0
     */
    constructor(error: KtConfigError) : this(listOf(error))
}
