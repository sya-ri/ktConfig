package dev.s7a.ktconfig.serializer

/**
 * Serializer for ULong type.
 * Handles conversion between ULong values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object ULongSerializer : ExtendSerializer<ULong, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toLong().toULong()

    override fun convertTo(value: ULong) = value.toLong()
}
