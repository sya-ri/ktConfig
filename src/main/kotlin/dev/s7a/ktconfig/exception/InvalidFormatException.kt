package dev.s7a.ktconfig.exception

/**
 * Exception thrown when a configuration value has an invalid format.
 *
 * @property text The text that caused the invalid format error
 * @property expected The expected format pattern
 * @since 2.0.0
 */
class InvalidFormatException(
    val text: String,
    val expected: String,
) : KtConfigException("Invalid format: $text, expected: $expected")
