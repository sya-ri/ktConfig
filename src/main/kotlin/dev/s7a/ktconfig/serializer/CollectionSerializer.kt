package dev.s7a.ktconfig.serializer

/**
 * Abstract serializer class that provides serialization functionality for collection types.
 *
 * @param E The type of elements in the collection
 * @param C The collection type
 * @property valueSerializer The serializer used for collection elements
 * @since 2.0.0
 */
abstract class CollectionSerializer<E, C>(
    val valueSerializer: Serializer<E>,
) : Serializer<C> {
    /**
     * Abstract serializer class that provides serialization functionality for nullable collection types.
     * Allows null values within the collection during serialization and deserialization.
     *
     * @param E The type of elements in the collection
     * @param C The collection type that may contain null elements
     * @property valueSerializer The serializer used for non-null collection elements
     * @since 2.0.0
     */
    abstract class Nullable<E, C>(
        val valueSerializer: Serializer<E>,
    ) : Serializer<C> {
        override fun deserialize(value: Any): C {
            val list = value as? List<*> ?: listOf(value)
            return list
                .map {
                    it?.let(valueSerializer::deserialize)
                }.let(::toCollection)
        }

        override fun serialize(value: C) =
            value?.let(::toList)?.map {
                it?.let(valueSerializer::serialize)
            }

        /**
         * Converts a List of elements into the specific collection type.
         *
         * @param value The list of elements to convert
         * @return The converted collection of type C
         * @since 2.0.0
         */
        abstract fun toCollection(value: List<E?>): C

        /**
         * Converts the specific collection type into a List of elements.
         *
         * @param value The collection to convert
         * @return The converted list of elements
         * @since 2.0.0
         */
        abstract fun toList(value: C): List<E?>
    }

    override fun deserialize(value: Any): C {
        val list = value as? List<*> ?: listOf(value)
        return list
            .map {
                valueSerializer.deserialize(it ?: throw IllegalArgumentException("Element of collection is null"))
            }.let(::toCollection)
    }

    override fun serialize(value: C) = value?.let(::toList)?.map(valueSerializer::serialize)

    /**
     * Converts a List of elements into the specific collection type.
     *
     * @param value The list of elements to convert
     * @return The converted collection of type C
     * @since 2.0.0
     */
    abstract fun toCollection(value: List<E>): C

    /**
     * Converts the specific collection type into a List of elements.
     *
     * @param value The collection to convert
     * @return The converted list of elements
     * @since 2.0.0
     */
    abstract fun toList(value: C): List<E>
}
