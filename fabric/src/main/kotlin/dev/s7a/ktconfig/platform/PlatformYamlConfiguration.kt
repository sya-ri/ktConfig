package dev.s7a.ktconfig.platform

import dev.s7a.ktconfig.exception.UnsupportedConvertException
import dev.s7a.ktconfig.fabric.YamlConfiguration
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.loader.ParsingException
import java.io.File

/**
 * YAML configuration type used by the Fabric backend.
 *
 * @since 2.3.0
 */
typealias PlatformYamlConfiguration = YamlConfiguration

/**
 * Fabric implementation of the shared YAML configuration contract.
 *
 * @since 2.3.0
 */
internal object PlatformYamlConfigurationAdapter : YamlConfigurationAdapter<PlatformYamlConfiguration> {
    override fun create(pathSeparator: Char) = PlatformYamlConfiguration(pathSeparator)

    override fun contains(
        configuration: PlatformYamlConfiguration,
        path: String,
    ) = configuration.contains(path)

    override fun get(
        configuration: PlatformYamlConfiguration,
        path: String,
    ) = configuration.get(path)

    override fun set(
        configuration: PlatformYamlConfiguration,
        path: String,
        value: Any?,
    ) {
        configuration.set(path, value)
    }

    override fun load(
        configuration: PlatformYamlConfiguration,
        file: File,
    ) {
        configuration.load(file)
    }

    override fun loadFromString(
        configuration: PlatformYamlConfiguration,
        content: String,
    ) {
        configuration.loadFromString(content)
    }

    override fun save(
        configuration: PlatformYamlConfiguration,
        file: File,
    ) {
        configuration.save(file)
    }

    override fun saveToString(configuration: PlatformYamlConfiguration) = configuration.saveToString()

    override fun setHeaderComment(
        configuration: PlatformYamlConfiguration,
        comment: List<String>,
    ) {
        configuration.setHeaderComment(comment)
    }

    override fun setComment(
        configuration: PlatformYamlConfiguration,
        path: String,
        comment: List<String>,
    ) {
        configuration.setComment(path, comment)
    }

    override fun asMap(value: Any): Map<*, *> =
        when (value) {
            is ConfigurationNode -> value.childrenMap().mapValues { it.value.raw() }
            is Map<*, *> -> value
            else -> throw UnsupportedConvertException(value::class, Map::class)
        }

    override fun isInvalidYamlException(cause: Throwable) = cause is ParsingException
}
