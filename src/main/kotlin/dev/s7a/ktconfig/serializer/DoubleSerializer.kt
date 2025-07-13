package dev.s7a.ktconfig.serializer

/**
 * Serializer for Double type.
 * Handles conversion between Double values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object DoubleSerializer : ExtendSerializer<Double, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toDouble()

    override fun convertTo(value: Double) = value
}
