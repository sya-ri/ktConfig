package dev.s7a.ktconfig.serializer

/**
 * Serializer for Short type.
 * Handles conversion between Short values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object ShortSerializer : TransformSerializer<Short, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toShort()

    override fun transformBack(value: Short) = value
}
