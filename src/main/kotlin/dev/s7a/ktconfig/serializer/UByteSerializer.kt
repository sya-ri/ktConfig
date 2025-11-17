package dev.s7a.ktconfig.serializer

/**
 * Serializer for [UByte] type.
 * Handles conversion between [UByte] values and YAML configuration using Number as base type.
 *
 * @since 2.0.0
 */
object UByteSerializer : TransformSerializer.Keyable<UByte, Number>(NumberSerializer) {
    override fun transform(value: Number) = value.toInt().toUByte()

    override fun transformBack(value: UByte) = value.toInt()
}
