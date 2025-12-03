package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Char] type.
 * Handles conversion between [Char] values and YAML configuration.
 *
 * @since 2.0.0
 */
object CharSerializer : TransformSerializer.Keyable<Char, String>(StringSerializer) {
    override fun decode(value: String) = value.single()

    override fun encode(value: Char) = value.toString()
}
