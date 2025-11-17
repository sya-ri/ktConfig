package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [BooleanArray] collections.
 * Handles serialization and deserialization of [BooleanArray] types using the provided value serializer.
 *
 * @since 2.0.0
 */
object BooleanArraySerializer : CollectionSerializer<Boolean, BooleanArray>(BooleanSerializer) {
    override fun toCollection(value: List<Boolean>) = value.toBooleanArray()

    override fun toList(value: BooleanArray) = value.toList()
}
