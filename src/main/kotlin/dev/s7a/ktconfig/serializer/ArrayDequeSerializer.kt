package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [ArrayDeque] collections.
 * Handles serialization and deserialization of [ArrayDeque]<[T]> types using the provided value serializer.
 *
 * @param T The type of elements in the array deque
 * @param valueSerializer The serializer used to handle individual array deque elements
 * @since 2.0.0
 */
class ArrayDequeSerializer<T>(
    valueSerializer: Serializer<T>,
) : CollectionSerializer<T, ArrayDeque<T>>(valueSerializer) {
    /**
     * Nullable variant of [ArrayDequeSerializer] that allows null elements.
     *
     * @param E The type of elements in the array deque
     * @param valueSerializer The serializer used to handle individual array deque elements
     * @since 2.0.0
     */
    class Nullable<E>(
        valueSerializer: Serializer<E>,
    ) : CollectionSerializer.Nullable<E, ArrayDeque<E?>>(valueSerializer) {
        override fun toCollection(value: List<E?>) = ArrayDeque(value)

        override fun toList(value: ArrayDeque<E?>) = value.toList()
    }

    override fun toCollection(value: List<T>) = ArrayDeque(value)

    override fun toList(value: ArrayDeque<T>) = value.toList()
}
