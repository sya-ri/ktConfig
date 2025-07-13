package dev.s7a.ktconfig.serializer

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
     * Gets a value from the configuration at the specified path or throws an exception if not found.
     *
     * @param configuration The YAML configuration to read from
     * @param path The configuration path to read the value from
     * @return The deserialized value of type [T]
     * @throws IllegalArgumentException if no value exists at the specified path
     * @since 2.0.0
     */
    fun getOrThrow(
        configuration: YamlConfiguration,
        path: String,
    ): T = get(configuration, path) ?: throw IllegalArgumentException("Not found value: $path")

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
    ): T?

    /**
     * Saves a value to the configuration at the specified path.
     *
     * @param configuration The YAML configuration to write to
     * @param path The configuration path to save the value at
     * @param value The value to serialize and save, or null to remove the value
     * @since 2.0.0
     */
    fun save(
        configuration: YamlConfiguration,
        path: String,
        value: T?,
    )
}
