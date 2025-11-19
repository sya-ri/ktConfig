package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.UnsupportedConvertException

/**
 * Serializer for [Boolean] type.
 * Handles conversion between [Boolean] values and YAML configuration.
 *
 * @since 2.0.0
 */
object BooleanSerializer : Serializer.Keyable<Boolean> {
    override fun deserialize(value: Any) =
        when (value) {
            is Boolean -> value
            is String -> value.toBoolean()
            else -> throw UnsupportedConvertException(value::class, Boolean::class)
        }

    override fun serialize(value: Boolean) = value
}
