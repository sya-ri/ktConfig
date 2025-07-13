package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for ArrayDeque collections.
 * Handles serialization and deserialization of ArrayDeque<E> types using the provided value serializer.
 *
 * @param E The type of elements in the array deque
 * @param valueSerializer The serializer used to handle individual array deque elements
 * @since 2.0.0
 */
class ArrayDequeSerializer<E>(
    valueSerializer: ValueSerializer<E>,
) : CollectionSerializer<E, ArrayDeque<E>>(valueSerializer) {
    override fun toCollection(value: List<E>) = ArrayDeque(value)

    override fun toList(value: ArrayDeque<E>) = value.toList()
}
