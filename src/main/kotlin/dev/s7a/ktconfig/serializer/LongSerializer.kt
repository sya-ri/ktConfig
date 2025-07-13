package dev.s7a.ktconfig.serializer

/**
 * Serializer for Long type.
 * Handles conversion between Long values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object LongSerializer : TransformSerializer<Long, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toLong()

    override fun transformBack(value: Long) = value
}
