package dev.s7a.ktconfig.exception

import dev.s7a.ktconfig.KtConfigError
import dev.s7a.ktconfig.format

/**
 * Exception thrown when config loading collects one or more errors.
 *
 * @property errors All loading errors collected before throwing.
 * @since 2.2.0
 */
class KtConfigLoadException(
    val errors: List<KtConfigError>,
) : KtConfigException(errors.format()) {
    /**
     * Creates an exception for a single aggregated loading error.
     *
     * @since 2.2.0
     */
    constructor(error: KtConfigError) : this(listOf(error))
}
