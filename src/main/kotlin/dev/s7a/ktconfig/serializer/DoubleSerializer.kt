package dev.s7a.ktconfig.serializer

/**
 * Serializer for Double type.
 * Handles conversion between Double values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object DoubleSerializer : TransformSerializer<Double, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toDouble()

    override fun transformBack(value: Double) = value
}
