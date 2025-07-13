package dev.s7a.ktconfig.serializer

/**
 * Serializer for Float type.
 * Handles conversion between Float values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object FloatSerializer : ExtendSerializer<Float, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toFloat()

    override fun to(value: Float) = value
}
