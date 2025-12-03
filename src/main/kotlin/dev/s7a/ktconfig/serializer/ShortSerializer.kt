package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Short] type.
 * Handles conversion between [Short] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object ShortSerializer : TransformSerializer.Keyable<Short, Number>(NumberSerializer) {
    override fun decode(value: Number) = value.toShort()

    override fun encode(value: Short) = value
}
