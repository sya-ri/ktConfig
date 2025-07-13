package dev.s7a.ktconfig.serializer

/**
 * Serializer for UByte type.
 * Handles conversion between UByte values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object UByteSerializer : ExtendSerializer<UByte, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toInt().toUByte()

    override fun to(value: UByte) = value.toInt()
}
