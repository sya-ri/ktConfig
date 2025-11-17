package dev.s7a.ktconfig.serializer

/**
 * Serializer for [ULong] type.
 * Handles conversion between [ULong] values and YAML configuration using [Number] as base type.
 *
 * @since 2.0.0
 */
object ULongSerializer : TransformSerializer.Keyable<ULong, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toLong().toULong()

    override fun transformBack(value: ULong) = value.toLong()
}
