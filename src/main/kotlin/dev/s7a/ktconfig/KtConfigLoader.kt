package dev.s7a.ktconfig

import dev.s7a.ktconfig.serializer.AnySerializer
import dev.s7a.ktconfig.serializer.MapSerializer
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * Abstract class for loading and saving configuration data to/from YAML files.
 * Provides basic functionality for serialization and deserialization of configuration objects.
 *
 * @param T The type of configuration object to load/save
 * @since 2.0.0
 */
abstract class KtConfigLoader<T> :
    TransformSerializer<T, Map<String, Any?>>(
        MapSerializer.Nullable(
            StringSerializer,
            AnySerializer,
        ),
    ) {
    companion object {
        /**
         * Change the path separator to be able to use Double or Float as a key
         */
        const val PATH_SEPARATOR = 0x00.toChar()
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
                if (file.exists()) {
                    load(file)
                }
            },
        )

    /**
     * Loads configuration data from a file and immediately saves it back.
     * This is useful for updating the file with default values or normalizing the format.
     *
     * @param file The file to load configuration from and save back to
     * @return The loaded configuration object of type T
     * @since 2.1.0
     */
    fun loadAndSave(file: File) =
        load(file).also {
            save(file, it)
        }

    /**
     * Loads configuration data from a file. If the file does not exist, loads the default configuration and saves it to the file.
     * This is useful for creating configuration files with default values on the first run.
     *
     * @param file The file to load configuration from
     * @return The loaded configuration object of type T
     * @since 2.1.0
     */
    fun loadAndSaveIfNotExists(file: File) =
        load(file).also {
            saveIfNotExists(file, it)
        }

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
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @return The loaded configuration object of type T
     * @since 2.0.0
     */
    abstract fun load(
        configuration: YamlConfiguration,
        parentPath: String = "",
    ): T

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
     * Saves configuration data to a file if not exists.
     *
     * @param file The file to save configuration to
     * @param value The configuration object to save
     * @since 2.1.0
     */
    fun saveIfNotExists(
        file: File,
        value: T,
    ) {
        if (file.exists().not()) {
            save(file, value)
        }
    }

    /**
     * Saves configuration data to a string.
     *
     * @param value The configuration object to save
     * @return The saved configuration as a YAML string
     * @since 2.0.0
     */
    fun saveToString(value: T) =
        configuration()
            .apply {
                save(this, value)
            }.saveToString()

    /**
     * Abstract method to save configuration data to a YamlConfiguration object.
     *
     * @param configuration The YamlConfiguration object to save to
     * @param value The configuration object to save
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @since 2.0.0
     */
    abstract fun save(
        configuration: YamlConfiguration,
        value: T,
        parentPath: String = "",
    )

    protected fun setHeaderComment(
        configuration: YamlConfiguration,
        comment: List<String>,
    ) {
        Reflection.setHeaderComment(configuration.options(), comment)
    }

    protected fun setHeaderComment(
        configuration: YamlConfiguration,
        parentPath: String,
        comment: List<String>,
    ) {
        if (parentPath.isEmpty()) {
            setHeaderComment(configuration, comment)
        } else {
            setComment(configuration, parentPath, comment)
        }
    }

    protected fun setComment(
        configuration: YamlConfiguration,
        path: String,
        comment: List<String>,
    ) {
        Reflection.setComment(configuration, path, comment)
    }
}
