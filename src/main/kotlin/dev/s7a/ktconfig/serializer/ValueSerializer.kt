package dev.s7a.ktconfig.serializer

import org.bukkit.configuration.file.YamlConfiguration

/**
 * Interface for serializing and deserializing individual values in YAML configuration.
 * Extends [Serializer] to provide specific value handling functionality.
 *
 * @param T The type of value being serialized/deserialized
 * @since 2.0.0
 */
interface ValueSerializer<T> : Serializer<T> {
    override fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T? = configuration.get(path)?.let(::deserialize)

    override fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    ) = configuration.set(path, value?.let(::serialize))

    /**
     * Deserializes a raw configuration value into type T.
     *
     * @param value The raw value from configuration
     * @return The deserialized value of type T
     * @since 2.0.0
     */
    fun deserialize(value: Any): T

    /**
     * Serializes a value of type T into a configuration-compatible format.
     *
     * @param value The value to serialize
     * @return The serialized value, or null if the value cannot be serialized
     * @since 2.0.0
     */
    fun serialize(value: T): Any?
}
