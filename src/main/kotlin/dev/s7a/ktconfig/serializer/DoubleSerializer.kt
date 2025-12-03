package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Double] type.
 * Handles conversion between [Double] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object DoubleSerializer : TransformSerializer.Keyable<Double, Number>(NumberSerializer) {
    override fun decode(value: Number) = value.toDouble()

    override fun encode(value: Double) = value
}
