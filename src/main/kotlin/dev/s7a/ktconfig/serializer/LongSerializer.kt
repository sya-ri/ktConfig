package dev.s7a.ktconfig.serializer

/**
 * Serializer for Long type.
 * Handles conversion between Long values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object LongSerializer : ExtendSerializer<Long, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toLong()

    override fun convertTo(value: Long) = value
}
