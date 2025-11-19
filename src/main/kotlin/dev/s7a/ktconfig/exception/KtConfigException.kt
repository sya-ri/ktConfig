package dev.s7a.ktconfig.exception

/**
 * Exception thrown for general configuration-related errors in the KtConfig library.
 *
 * @property message The error message describing the configuration error
 * @since 2.0.0
 */
open class KtConfigException(
    override val message: String,
) : Exception(message)
