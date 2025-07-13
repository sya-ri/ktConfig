package dev.s7a.ktconfig

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * Abstract class for loading and saving configuration data to/from YAML files.
 * Provides basic functionality for serialization and deserialization of configuration objects.
 *
 * @param T The type of configuration object to load/save
 * @since 2.0.0
 */
abstract class KtConfigLoader<T> {
    companion object {
        /**
         * Change the path separator to be able to use Double or Float as a key
         */
        private const val PATH_SEPARATOR = 0x00.toChar()
    }

    private fun configuration() =
        YamlConfiguration().apply {
            options().pathSeparator(PATH_SEPARATOR)
        }

    /**
     * Loads configuration data from a file.
     *
     * @param file The file to load configuration from
     * @return The loaded configuration object of type T
     * @since 2.0.0
     */
    fun load(file: File) =
        load(
            configuration().apply {
                load(file)
            },
        )

    /**
     * Loads configuration data from a string content.
     *
     * @param content The YAML content string to load configuration from
     * @return The loaded configuration object of type T
     * @since 2.0.0
     */
    fun loadFromString(content: String) =
        load(
            configuration().apply {
                loadFromString(content)
            },
        )

    /**
     * Abstract method to load configuration data from a YamlConfiguration object.
     *
     * @param configuration The YamlConfiguration object to load from
     * @return The loaded configuration object of type T
     * @since 2.0.0
     */
    protected abstract fun load(configuration: YamlConfiguration): T

    /**
     * Saves configuration data to a file.
     *
     * @param file The file to save configuration to
     * @param value The configuration object to save
     * @since 2.0.0
     */
    fun save(
        file: File,
        value: T,
    ) = configuration()
        .apply {
            save(this, value)
        }.save(file)

    /**
     * Saves configuration data to a string.
     *
     * @param file The file to save configuration to
     * @param value The configuration object to save
     * @return The saved configuration as a YAML string
     * @since 2.0.0
     */
    fun saveToString(
        file: File,
        value: T,
    ) = configuration()
        .apply {
            save(this, value)
        }.saveToString()

    /**
     * Abstract method to save configuration data to a YamlConfiguration object.
     *
     * @param configuration The YamlConfiguration object to save to
     * @param value The configuration object to save
     * @since 2.0.0
     */
    protected abstract fun save(
        configuration: YamlConfiguration,
        value: T,
    )
}
