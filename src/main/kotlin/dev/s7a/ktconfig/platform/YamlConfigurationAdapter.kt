package dev.s7a.ktconfig.platform

import java.io.File

/**
 * Common adapter for the YAML operations that differ between supported platforms.
 *
 * ktConfig's loader and serializer implementations use this contract so their public behavior is defined once,
 * while Bukkit and Fabric retain their native YAML configuration types in public method signatures.
 *
 * @param C The platform-specific YAML configuration type
 * @since 2.3.0
 */
internal interface YamlConfigurationAdapter<C> {
    /**
     * Creates an empty configuration using [pathSeparator] for nested paths.
     *
     * @param pathSeparator The character used to separate nested path segments
     * @return An empty platform configuration
     * @since 2.3.0
     */
    fun create(pathSeparator: Char): C

    /**
     * Returns whether [path] exists in [configuration].
     *
     * @param configuration The configuration to inspect
     * @param path The path to inspect
     * @return True when the path exists
     * @since 2.3.0
     */
    fun contains(
        configuration: C,
        path: String,
    ): Boolean

    /**
     * Returns the raw value stored at [path].
     *
     * @param configuration The configuration to read from
     * @param path The path to read
     * @return The raw value, or null when no value exists
     * @since 2.3.0
     */
    fun get(
        configuration: C,
        path: String,
    ): Any?

    /**
     * Stores [value] at [path].
     *
     * @param configuration The configuration to write to
     * @param path The path to write
     * @param value The raw value to store
     * @since 2.3.0
     */
    fun set(
        configuration: C,
        path: String,
        value: Any?,
    )

    /**
     * Loads [configuration] from [file].
     *
     * @param configuration The configuration to populate
     * @param file The YAML file to load
     * @since 2.3.0
     */
    fun load(
        configuration: C,
        file: File,
    )

    /**
     * Loads [configuration] from YAML [content].
     *
     * @param configuration The configuration to populate
     * @param content The YAML content to load
     * @since 2.3.0
     */
    fun loadFromString(
        configuration: C,
        content: String,
    )

    /**
     * Saves [configuration] to [file].
     *
     * @param configuration The configuration to save
     * @param file The destination YAML file
     * @since 2.3.0
     */
    fun save(
        configuration: C,
        file: File,
    )

    /**
     * Serializes [configuration] to YAML.
     *
     * @param configuration The configuration to serialize
     * @return The serialized YAML content
     * @since 2.3.0
     */
    fun saveToString(configuration: C): String

    /**
     * Sets the document header [comment].
     *
     * @param configuration The configuration to update
     * @param comment The header comment lines
     * @since 2.3.0
     */
    fun setHeaderComment(
        configuration: C,
        comment: List<String>,
    )

    /**
     * Sets [comment] for [path].
     *
     * @param configuration The configuration to update
     * @param path The path whose comment is updated
     * @param comment The comment lines
     * @since 2.3.0
     */
    fun setComment(
        configuration: C,
        path: String,
        comment: List<String>,
    )

    /**
     * Converts a platform-specific map representation to a standard [Map].
     *
     * @param value The raw platform value
     * @return The value represented as a map
     * @throws dev.s7a.ktconfig.exception.UnsupportedConvertException if [value] is not map-like
     * @since 2.3.0
     */
    fun asMap(value: Any): Map<*, *>

    /**
     * Returns whether [cause] represents malformed YAML on this platform.
     *
     * @param cause The exception to inspect
     * @return True when the exception was caused by malformed YAML
     * @since 2.3.0
     */
    fun isInvalidYamlException(cause: Throwable): Boolean
}
