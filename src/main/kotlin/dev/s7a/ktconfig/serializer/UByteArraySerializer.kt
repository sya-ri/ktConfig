package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [UByteArray] collections.
 * Handles serialization and deserialization of UByteArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
object UByteArraySerializer : CollectionSerializer<UByte, UByteArray>(UByteSerializer) {
    override fun toCollection(value: List<UByte>) = value.toUByteArray()

    override fun toList(value: UByteArray) = value.toList()
}
