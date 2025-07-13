package dev.s7a.ktconfig.serializer

/**
 * Serializer for String type.
 * Handles conversion between String values and YAML configuration.
 *
 * @since 2.0.0
 */
object StringSerializer : ValueSerializer<String> {
    override fun from(value: Any) = value.toString()

    override fun to(value: String) = value
}
