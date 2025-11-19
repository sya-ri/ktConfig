package dev.s7a.ktconfig.exception

/**
 * Exception thrown when a value is not found at a specified configuration path.
 *
 * @property path The configuration path where the value was not found
 * @since 2.0.0
 */
class NotFoundValueException(
    val path: String,
) : KtConfigException("Not found value: $path")
