package dev.s7a.ktconfig.serializer

/**
 * Serializer for [UInt] type.
 * Handles conversion between [UInt] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object UIntSerializer : TransformSerializer.Keyable<UInt, Number>(NumberSerializer) {
    override fun decode(value: Number) = value.toLong().toUInt()

    override fun encode(value: UInt) = value.toLong()
}
