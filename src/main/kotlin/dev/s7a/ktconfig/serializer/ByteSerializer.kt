package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Byte] type.
 * Handles conversion between [Byte] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object ByteSerializer : TransformSerializer.Keyable<Byte, Number>(NumberSerializer) {
    override fun decode(value: Number) = value.toByte()

    override fun encode(value: Byte) = value
}
