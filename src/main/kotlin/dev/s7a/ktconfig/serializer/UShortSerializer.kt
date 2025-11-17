package dev.s7a.ktconfig.serializer

/**
 * Serializer for [UShort] type.
 * Handles conversion between UShort values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object UShortSerializer : TransformSerializer.Keyable<UShort, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toInt().toUShort()

    override fun transformBack(value: UShort) = value.toInt()
}
