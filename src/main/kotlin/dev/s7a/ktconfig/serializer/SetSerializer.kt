package dev.s7a.ktconfig.serializer

/**
 * Serializer for Set collections that handles serialization and deserialization of Set elements.
 *
 * @param E The type of elements in the Set
 * @param valueSerializer The serializer used for Set elements
 * @since 2.0.0
 */
class SetSerializer<E>(
    valueSerializer: ValueSerializer<E>,
) : CollectionSerializer<E, Set<E>>(valueSerializer) {
    override fun toCollection(value: List<E>) = value.toSet()

    override fun toList(value: Set<E>) = value.toList()
}
