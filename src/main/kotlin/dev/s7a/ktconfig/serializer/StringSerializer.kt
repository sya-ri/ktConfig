package dev.s7a.ktconfig.serializer

/**
 * Serializer for String type.
 * Handles conversion between String values and YAML configuration.
 *
 * @since 2.0.0
 */
object StringSerializer : Serializer.Keyable<String> {
    override fun deserialize(value: Any) = value.toString()

    override fun serialize(value: String) = value
}
