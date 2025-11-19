package dev.s7a.ktconfig.exception

/**
 * Exception thrown when a configuration value has an invalid format.
 *
 * @property text The text that caused the invalid format error
 * @since 2.0.0
 */
class InvalidFormatException(
    val text: String,
) : KtConfigException("Invalid format: $text")
