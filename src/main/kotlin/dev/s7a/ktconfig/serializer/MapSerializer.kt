package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.NullValueException
import dev.s7a.ktconfig.exception.UnsupportedConvertException
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
    /**
     * Nullable variant of [MapSerializer] that allows null values in the map.
     *
     * @param K The type of map keys
     * @param V The type of map values
     * @property keySerializer The serializer used for map keys
     * @property valueSerializer The serializer used for map values
     * @since 2.0.0
     */
    class Nullable<K, V>(
        val keySerializer: Serializer.Keyable<K>,
        val valueSerializer: Serializer<V>,
    ) : Serializer<Map<K, V?>> {
        override fun deserialize(value: Any) =
            when (value) {
                is MemorySection -> value.getValues(false)
                is Map<*, *> -> value
                else -> throw UnsupportedConvertException(value::class, Map::class)
            }.mapKeys {
                keySerializer.deserialize(it.key ?: throw NullValueException())
            }.mapValues {
                it.value?.let(valueSerializer::deserialize)
            }

        override fun serialize(value: Map<K, V?>) =
            value
                .mapKeys {
                    keySerializer.serialize(it.key)
                }.mapValues {
                    it.value?.let(valueSerializer::serialize)
                }
    }

    override fun deserialize(value: Any) =
        when (value) {
            is MemorySection -> value.getValues(false)
            is Map<*, *> -> value
            else -> throw UnsupportedConvertException(value::class, Map::class)
        }.mapKeys {
            keySerializer.deserialize(it.key ?: throw NullValueException())
        }.mapValues {
            valueSerializer.deserialize(it.value ?: throw NullValueException())
        }

    override fun serialize(value: Map<K, V>) =
        value
            .mapKeys {
                keySerializer.serialize(it.key)
            }.mapValues {
                valueSerializer.serialize(it.value)
            }
}
