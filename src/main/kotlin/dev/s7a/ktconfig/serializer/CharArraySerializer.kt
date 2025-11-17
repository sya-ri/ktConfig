package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [CharArray] collections.
 * Handles serialization and deserialization of [CharArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object CharArraySerializer : CollectionSerializer<Char, CharArray>(CharSerializer) {
    override fun toCollection(value: List<Char>) = value.toCharArray()

    override fun toList(value: CharArray) = value.toList()
}
