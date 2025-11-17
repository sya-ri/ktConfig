package dev.s7a.ktconfig.serializer

/**
 * Serializer for Set collections that handles serialization and deserialization of Set elements.
 *
 * @param E The type of elements in the Set
 * @param valueSerializer The serializer used for Set elements
 * @since 2.0.0
 */
class SetSerializer<E>(
    valueSerializer: Serializer<E>,
) : CollectionSerializer<E, Set<E>>(valueSerializer) {
    /**
     * Nullable variant of [SetSerializer] that allows null elements.
     *
     * @param E The type of elements in the set
     * @param valueSerializer The serializer used to handle individual set elements
     * @since 2.0.0
     */
    class Nullable<E>(
        valueSerializer: Serializer<E>,
    ) : CollectionSerializer.Nullable<E, Set<E?>>(valueSerializer) {
        override fun toCollection(value: List<E?>) = value.toSet()

        override fun toList(value: Set<E?>) = value.toList()
    }

    override fun toCollection(value: List<E>) = value.toSet()

    override fun toList(value: Set<E>) = value.toList()
}
