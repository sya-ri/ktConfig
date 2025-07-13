package dev.s7a.ktconfig.serializer

/**
 * Serializer for Int type.
 * Handles conversion between Int values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object IntSerializer : ExtendSerializer<Int, Number>(NumberSerializer) {
    override fun from(base: Number) = base.toInt()

    override fun to(value: Int) = value
}
