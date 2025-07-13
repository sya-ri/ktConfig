package dev.s7a.ktconfig.serializer

/**
 * Serializer for UInt type.
 * Handles conversion between UInt values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object UIntSerializer : ExtendSerializer<UInt, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toLong().toUInt()

    override fun to(value: UInt) = value.toLong()
}
