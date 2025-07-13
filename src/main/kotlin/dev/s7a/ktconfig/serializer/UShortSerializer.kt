package dev.s7a.ktconfig.serializer

/**
 * Serializer for UShort type.
 * Handles conversion between UShort values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object UShortSerializer : ExtendSerializer<UShort, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toInt().toUShort()

    override fun convertTo(value: UShort) = value.toInt()
}
