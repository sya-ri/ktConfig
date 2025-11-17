package dev.s7a.ktconfig.serializer

/**
 * Serializer for [Set] collections that handles serialization and deserialization of Set elements.
 *
 * @param T The type of elements in the [Set]
 * @param valueSerializer The serializer used for Set elements
 * @since 2.0.0
 */
class SetSerializer<T>(
    valueSerializer: Serializer<T>,
) : CollectionSerializer<T, Set<T>>(valueSerializer) {
    /**
     * Nullable variant of [SetSerializer] that allows null elements.
     *
     * @param T The type of elements in the set
     * @param valueSerializer The serializer used to handle individual set elements
     * @since 2.0.0
     */
    class Nullable<T>(
        valueSerializer: Serializer<T>,
    ) : CollectionSerializer.Nullable<T, Set<T?>>(valueSerializer) {
        override fun toCollection(value: List<T?>) = value.toSet()

        override fun toList(value: Set<T?>) = value.toList()
    }

    override fun toCollection(value: List<T>) = value.toSet()

    override fun toList(value: Set<T>) = value.toList()
}
