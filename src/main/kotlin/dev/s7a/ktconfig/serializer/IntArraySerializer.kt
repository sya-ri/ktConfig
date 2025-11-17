package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [IntArray] collections.
 * Handles serialization and deserialization of [IntArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object IntArraySerializer : CollectionSerializer<Int, IntArray>(IntSerializer) {
    override fun toCollection(value: List<Int>) = value.toIntArray()

    override fun toList(value: IntArray) = value.toList()
}
