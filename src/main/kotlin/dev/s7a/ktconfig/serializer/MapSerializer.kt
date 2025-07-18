package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.MemorySection

/**
 * Serializer class that provides serialization functionality for map types.
 *
 * @param K The type of map keys
 * @param V The type of map values
 * @property keySerializer The serializer used for map keys
 * @property valueSerializer The serializer used for map values
 * @since 2.0.0
 */
class MapSerializer<K, V>(
    val keySerializer: Serializer.Keyable<K>,
    val valueSerializer: Serializer<V>,
) : Serializer<Map<K, V>> {
    override fun deserialize(value: Any) =
        when (value) {
            is MemorySection -> value.getValues(false)
            is Map<*, *> -> value
            else -> throw IllegalArgumentException("Cannot convert to Map: ${value::class.simpleName}")
        }.mapKeys {
            keySerializer.deserialize(it.key ?: throw IllegalArgumentException("Map key cannot be null"))
        }.mapValues {
            valueSerializer.deserialize(it.value ?: throw IllegalArgumentException("Map value cannot be null"))
        }

    override fun serialize(value: Map<K, V>) =
        value
            .mapKeys {
                keySerializer.serialize(it.key)
            }.mapValues {
                valueSerializer.serialize(it.value)
            }
}
