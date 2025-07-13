package dev.s7a.ktconfig.serializer

/**
 * Serializer for Short type.
 * Handles conversion between Short values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object ShortSerializer : ExtendSerializer<Short, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toShort()

    override fun convertTo(value: Short) = value
}
