package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for LongArray collections.
 * Handles serialization and deserialization of LongArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
object LongArraySerializer : CollectionSerializer<Long, LongArray>(LongSerializer) {
    override fun toCollection(value: List<Long>) = value.toLongArray()

    override fun toList(value: LongArray) = value.toList()
} 
