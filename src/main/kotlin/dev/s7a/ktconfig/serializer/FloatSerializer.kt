package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Float] type.
 * Handles conversion between [Float] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object FloatSerializer : TransformSerializer.Keyable<Float, Number>(NumberSerializer) {
    override fun decode(value: Number) = value.toFloat()

    override fun encode(value: Float) = value
}
