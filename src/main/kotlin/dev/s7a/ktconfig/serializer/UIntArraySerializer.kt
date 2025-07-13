package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for UIntArray collections.
 * Handles serialization and deserialization of UIntArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
object UIntArraySerializer : CollectionSerializer<UInt, UIntArray>(UIntSerializer) {
    override fun toCollection(value: List<UInt>) = value.toUIntArray()

    override fun toList(value: UIntArray) = value.toList()
}
