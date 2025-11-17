package dev.s7a.ktconfig.serializer

import java.math.BigDecimal

/**
 * Serializer implementation for [BigDecimal] values.
 * Handles serialization and deserialization of [BigDecimal] types by converting them to and from String representation.
 *
 * @since 2.0.0
 */
object BigDecimalSerializer : TransformSerializer.Keyable<BigDecimal, Number>(NumberSerializer) {
    override fun transform(value: Number) = BigDecimal(value.toString())

    override fun transformBack(value: BigDecimal) = value
}
