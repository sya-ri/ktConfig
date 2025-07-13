package dev.s7a.ktconfig.serializer

/**
 * Serializer for ULong type.
 * Handles conversion between ULong values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object ULongSerializer : ExtendSerializer<ULong, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toLong().toULong()

    override fun to(value: ULong) = value.toLong()
}
