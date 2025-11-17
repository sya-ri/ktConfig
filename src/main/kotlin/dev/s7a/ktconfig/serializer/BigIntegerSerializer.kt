package dev.s7a.ktconfig.serializer

import java.math.BigInteger

/**
 * Serializer implementation for [BigInteger] values.
 * Handles serialization and deserialization of [BigInteger] types by converting them to and from String representation.
 *
 * @since 2.0.0
 */
object BigIntegerSerializer : TransformSerializer.Keyable<BigInteger, Number>(NumberSerializer) {
    override fun transform(value: Number) = BigInteger(value.toString())

    override fun transformBack(value: BigInteger) = value
}
