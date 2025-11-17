package dev.s7a.ktconfig.serializer

/**
 * Serializer implementation for [List] collections.
 * Handles serialization and deserialization of [List]<[T]> types using the provided value serializer.
 *
 * @param T The type of elements in the list
 * @param valueSerializer The serializer used to handle individual list elements
 * @since 2.0.0
 */
class ListSerializer<T>(
    valueSerializer: Serializer<T>,
) : CollectionSerializer<T, List<T>>(valueSerializer) {
    /**
     * Nullable variant of [ListSerializer] that allows null elements.
     *
     * @param T The type of elements in the list
     * @param valueSerializer The serializer used to handle individual list elements
     * @since 2.0.0
     */
    class Nullable<T>(
        valueSerializer: Serializer<T>,
    ) : CollectionSerializer.Nullable<T, List<T?>>(valueSerializer) {
        override fun toCollection(value: List<T?>) = value

        override fun toList(value: List<T?>) = value
    }

    override fun toCollection(value: List<T>) = value

    override fun toList(value: List<T>) = value
}
