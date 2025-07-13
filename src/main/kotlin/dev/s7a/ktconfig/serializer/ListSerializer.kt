package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for List collections.
 * Handles serialization and deserialization of List<E> types using the provided value serializer.
 *
 * @param E The type of elements in the list
 * @param valueSerializer The serializer used to handle individual list elements
 * @since 2.0.0
 */
class ListSerializer<E>(
    valueSerializer: ValueSerializer<E>,
) : CollectionSerializer<E, List<E>>(valueSerializer) {
    override fun toCollection(value: List<E>) = value

    override fun toList(value: List<E>) = value
}
