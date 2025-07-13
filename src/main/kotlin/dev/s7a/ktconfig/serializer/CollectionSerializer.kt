package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

/**
 * Abstract serializer class that provides serialization functionality for collection types.
 *
 * @param E The type of elements in the collection
 * @param C The collection type
 * @property valueSerializer The serializer used for collection elements
 * @since 2.0.0
 */
abstract class CollectionSerializer<E, C>(
    val valueSerializer: ValueSerializer<E>,
) : Serializer<C> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): C? =
        configuration
            .getList(path)
            ?.map { valueSerializer.deserialize(it!!) }
            ?.let(::toCollection)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: C?,
    ) {
        configuration.set(path, value?.let(::toList)?.map(valueSerializer::serialize))
    }

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
