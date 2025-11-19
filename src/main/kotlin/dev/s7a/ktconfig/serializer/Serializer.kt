package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.NotFoundValueException
import org.bukkit.configuration.file.YamlConfiguration

/**
 * Interface for handling serialization and deserialization of values to/from YAML configuration.
 * This interface provides methods to read and write values of type [T] in a YAML configuration.
 *
 * @param T The type of value to be serialized/deserialized
 * @since 2.0.0
 */
interface Serializer<T> {
    /**
     * A serializer interface for types that can be used as Map keys.
     * This interface extends [Serializer] and indicates that the type [T] can be safely used as keys in Map structures.
     *
     * @param T The type that can be used as Map keys
     * @since 2.0.0
     */
    interface Keyable<T> : Serializer<T>

    /**
     * Gets a value from the configuration at the specified path or throws an exception if not found.
     *
     * @param configuration The YAML configuration to read from
     * @param path The configuration path to read the value from
     * @return The deserialized value of type [T]
     * @throws NotFoundValueException if no value exists at the specified path
     * @since 2.0.0
     */
    fun getOrThrow(
        configuration: YamlConfiguration,
        path: String,
    ): T = get(configuration, path) ?: throw NotFoundValueException(path)

    /**
     * Gets a value from the configuration at the specified path.
     *
     * @param configuration The YAML configuration to read from
     * @param path The configuration path to read the value from
     * @return The deserialized value of type [T], or null if the path doesn't exist
     * @since 2.0.0
     */
    fun get(
        configuration: YamlConfiguration,
        path: String,
    ): T? = configuration.get(path)?.let(::deserialize)

    /**
     * Saves a value to the configuration at the specified path.
     *
     * @param configuration The YAML configuration to write to
     * @param path The configuration path to save the value at
     * @param value The value to serialize and save, or null to remove the value
     * @since 2.0.0
     */
    fun set(
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
