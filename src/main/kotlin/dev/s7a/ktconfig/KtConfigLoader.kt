package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.serializer.AnySerializer
import dev.s7a.ktconfig.serializer.MapSerializer
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

        /**
         * Converts an internal configuration path to a user-facing path.
         *
         * Path segments containing dots are quoted so YAML keys such as `2.0` are not confused with nested paths.
         *
         * @since 2.2.0
         */
        fun formatPath(path: String): String =
            if (path.isEmpty()) {
                ""
            } else {
                path
                    .split(PATH_SEPARATOR)
                    .joinToString(".") { it.quoteIfNeeded() }
            }

        private fun String.quoteIfNeeded(): String =
            if (isEmpty() || contains(".")) {
                "'${replace("'", "\\'")}'"
            } else {
                this
            }
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
    fun load(file: File) = loadResult(file).getOrThrow()

    /**
     * Loads configuration data from a file and returns either the loaded value or all loading errors.
     *
     * @param file The file to load configuration from
     * @return A result containing the loaded value or collected loading errors
     * @since 2.2.0
     */
    fun loadResult(file: File) =
        try {
            loadResult(
                configuration().apply {
                    if (file.exists()) {
                        load(file)
                    }
                },
            )
        } catch (e: Throwable) {
            KtConfigResult.Failure(KtConfigError.fromException("", e))
        }

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
    fun loadFromString(content: String) = loadResultFromString(content).getOrThrow()

    /**
     * Loads configuration data from a string content and returns either the loaded value or all loading errors.
     *
     * @param content The YAML content string to load configuration from
     * @return A result containing the loaded value or collected loading errors
     * @since 2.2.0
     */
    fun loadResultFromString(content: String) =
        try {
            loadResult(
                configuration().apply {
                    loadFromString(content)
                },
            )
        } catch (e: Throwable) {
            KtConfigResult.Failure(KtConfigError.fromException("", e))
        }

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
     * Executes automatic validation for values that implement [KtConfigValidatable].
     *
     * Generated loaders call this after constructing a config value. Validation errors are reported
     * as loading errors so [load], [loadResult], [decode], and [deserialize] share the same failure model.
     *
     * @param value The loaded value to validate
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @return [value] when validation succeeds
     * @throws KtConfigLoadException if validation returns one or more errors
     * @since 2.2.0
     */
    protected fun validateLoadedConfig(
        value: T,
        parentPath: String = "",
    ): T {
        @Suppress("UNCHECKED_CAST")
        val validatable = value as? KtConfigValidatable<T> ?: return value
        val errors =
            KtConfigValidatorBuilder<T>()
                .apply {
                    with(validatable) {
                        validate()
                    }
                }.build()
                .validate(value)
        if (errors.isNotEmpty()) {
            throw KtConfigLoadException(errors.map { it.withParentPath(parentPath) })
        }
        return value
    }

    private fun KtConfigError.withParentPath(parentPath: String): KtConfigError {
        if (parentPath.isEmpty()) {
            return copy(path = formatPath(path))
        }
        val parent = parentPath.removeSuffix(PATH_SEPARATOR.toString())
        return copy(
            path =
                if (path.isEmpty()) {
                    formatPath(parent)
                } else {
                    formatPath("$parentPath$path")
                },
        )
    }

    /**
     * Loads configuration data from a [YamlConfiguration] and returns either the loaded value or all loading errors.
     *
     * Generated loaders override [load] to collect multiple property errors before throwing [KtConfigLoadException].
     * Custom loaders can override this method directly when they need fully aggregated error reporting.
     *
     * @param configuration The YamlConfiguration object to load from
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @return A result containing the loaded value or collected loading errors
     * @since 2.2.0
     */
    fun loadResult(
        configuration: YamlConfiguration,
        parentPath: String = "",
    ): KtConfigResult<T> =
        try {
            KtConfigResult.Success(load(configuration, parentPath))
        } catch (e: KtConfigLoadException) {
            KtConfigResult.Failure(e.errors)
        } catch (e: Throwable) {
            KtConfigResult.Failure(KtConfigError.fromException(parentPath, e))
        }

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
