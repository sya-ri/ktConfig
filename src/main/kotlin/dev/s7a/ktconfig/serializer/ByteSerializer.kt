package dev.s7a.ktconfig.serializer

/**
 * Serializer for Byte type.
 * Handles conversion between Byte values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object ByteSerializer : ExtendSerializer<Byte, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toByte()

    override fun to(value: Byte) = value
}
