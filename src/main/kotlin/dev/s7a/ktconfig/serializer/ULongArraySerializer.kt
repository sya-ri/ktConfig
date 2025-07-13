package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for ULongArray collections.
 * Handles serialization and deserialization of ULongArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
object ULongArraySerializer : CollectionSerializer<ULong, ULongArray>(ULongSerializer) {
    override fun toCollection(value: List<ULong>) = value.toULongArray()

    override fun toList(value: ULongArray) = value.toList()
}
