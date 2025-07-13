package dev.s7a.ktconfig.serializer

/**
 * Serializer for Int type.
 * Handles conversion between Int values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object IntSerializer : ExtendSerializer<Int, Number>(NumberSerializer) {
    override fun convertFrom(value: Number) = value.toInt()

    override fun convertTo(value: Int) = value
}
