package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for UShortArray collections.
 * Handles serialization and deserialization of UShortArray types using the provided value serializer.
 *
 * @since 2.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
object UShortArraySerializer : CollectionSerializer<UShort, UShortArray>(UShortSerializer) {
    override fun toCollection(value: List<UShort>) = value.toUShortArray()

    override fun toList(value: UShortArray) = value.toList()
}
