package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [ShortArray] collections.
 * Handles serialization and deserialization of [ShortArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object ShortArraySerializer : CollectionSerializer<Short, ShortArray>(ShortSerializer) {
    override fun toCollection(value: List<Short>) = value.toShortArray()

    override fun toList(value: ShortArray) = value.toList()
}
