package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [DoubleArray] collections.
 * Handles serialization and deserialization of [DoubleArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object DoubleArraySerializer : CollectionSerializer<Double, DoubleArray>(DoubleSerializer) {
    override fun toCollection(value: List<Double>) = value.toDoubleArray()

    override fun toList(value: DoubleArray) = value.toList()
}
