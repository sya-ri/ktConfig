package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [ByteArray] collections.
 * Handles serialization and deserialization of [ByteArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object ByteArraySerializer : CollectionSerializer<Byte, ByteArray>(ByteSerializer) {
    override fun toCollection(value: List<Byte>) = value.toByteArray()

    override fun toList(value: ByteArray) = value.toList()
}
