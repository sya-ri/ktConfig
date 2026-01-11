package dev.s7a.ktconfig.exception

/**
 * Exception thrown when a discriminator value is invalid for a sealed interface or class.
 *
 * @property discriminator The discriminator value that was invalid
 * @since 2.0.0
 */
class InvalidDiscriminatorException(
    val discriminator: String,
) : KtConfigException("Invalid discriminator: $discriminator")
