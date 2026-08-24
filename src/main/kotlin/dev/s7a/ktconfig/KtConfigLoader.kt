package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.platform.PlatformYamlConfiguration
import dev.s7a.ktconfig.platform.PlatformYamlConfigurationAdapter
import dev.s7a.ktconfig.serializer.AnySerializer
import dev.s7a.ktconfig.serializer.MapSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
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
         * Internal path separator used so YAML keys containing dots, such as decimal map keys, remain single segments.
         *
         * @since 2.0.0
         */
        const val PATH_SEPARATOR = 0x00.toChar()

        /**
         * Converts an internal configuration path to a user-facing path.
         *
         * Path segments containing dots are quoted so YAML keys such as `2.0` are not confused with nested paths.
         *
         * @param path The internal path to format
         * @return The user-facing dotted path
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

    private fun configuration() = PlatformYamlConfigurationAdapter.create(PATH_SEPARATOR)

    /**
     * Loads configuration data from a file.
     *
     * @param file The file to load configuration from
     * @return The loaded configuration object of type T
     * @throws KtConfigLoadException if the configuration cannot be loaded
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
                        PlatformYamlConfigurationAdapter.load(this, file)
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
     * @throws KtConfigLoadException if the configuration cannot be loaded
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
     * @throws KtConfigLoadException if the configuration cannot be loaded
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
     * @throws KtConfigLoadException if the configuration cannot be loaded
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
                    PlatformYamlConfigurationAdapter.loadFromString(this, content)
                },
            )
        } catch (e: Throwable) {
            KtConfigResult.Failure(KtConfigError.fromException("", e))
        }

    /**
     * Abstract method to load configuration data from a platform YAML configuration.
     *
     * @param configuration The platform YAML configuration to load from
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @return The loaded configuration object of type T
     * @since 2.0.0
     */
    abstract fun load(
        configuration: PlatformYamlConfiguration,
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
     * Loads configuration data from a [PlatformYamlConfiguration] and returns either the loaded value or all loading errors.
     *
     * Generated loaders override [load] to collect multiple property errors before throwing [KtConfigLoadException].
     * Custom loaders can override this method directly when they need fully aggregated error reporting.
     *
     * @param configuration The platform YAML configuration to load from
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @return A result containing the loaded value or collected loading errors
     * @since 2.2.0
     */
    fun loadResult(
        configuration: PlatformYamlConfiguration,
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
    ) {
        val configuration = configuration()
        save(configuration, value)
        PlatformYamlConfigurationAdapter.save(configuration, file)
    }

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
        configuration().let { configuration ->
            save(configuration, value)
            PlatformYamlConfigurationAdapter.saveToString(configuration)
        }

    /**
     * Abstract method to save configuration data to a platform YAML configuration.
     *
     * @param configuration The platform YAML configuration to save to
     * @param value The configuration object to save
     * @param parentPath The path of the parent node, or an empty string if there is no parent node
     * @since 2.0.0
     */
    abstract fun save(
        configuration: PlatformYamlConfiguration,
        value: T,
        parentPath: String = "",
    )

    /**
     * Sets the root header comment for [configuration].
     *
     * @param configuration The platform YAML configuration to update
     * @param comment The header comment lines
     * @since 2.0.0
     */
    protected fun setHeaderComment(
        configuration: PlatformYamlConfiguration,
        comment: List<String>,
    ) {
        PlatformYamlConfigurationAdapter.setHeaderComment(configuration, comment)
    }

    /**
     * Sets a header comment at [parentPath], or the root header when [parentPath] is empty.
     *
     * @param configuration The platform YAML configuration to update
     * @param parentPath The path of the nested configuration, or an empty string for the root
     * @param comment The header comment lines
     * @since 2.0.0
     */
    protected fun setHeaderComment(
        configuration: PlatformYamlConfiguration,
        parentPath: String,
        comment: List<String>,
    ) {
        if (parentPath.isEmpty()) {
            setHeaderComment(configuration, comment)
        } else {
            setComment(configuration, parentPath, comment)
        }
    }

    /**
     * Sets a comment for [path].
     *
     * @param configuration The platform YAML configuration to update
     * @param path The configuration path to annotate
     * @param comment The comment lines
     * @since 2.0.0
     */
    protected fun setComment(
        configuration: PlatformYamlConfiguration,
        path: String,
        comment: List<String>,
    ) {
        PlatformYamlConfigurationAdapter.setComment(configuration, path, comment)
    }
}
