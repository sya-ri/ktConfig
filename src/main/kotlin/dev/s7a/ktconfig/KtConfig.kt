package dev.s7a.ktconfig

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Load config.
 *
 * @param serializer [KSerializer]<[T]>
 * @param file File
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
fun <T : Any> ktConfig(serializer: KSerializer<T>, file: File, yaml: Yaml = Yaml.default): T? {
    val text = if (file.exists()) file.readText() else return null
    if (text.isBlank()) return null
    return yaml.decodeFromString(serializer, text)
}

/**
 * Load config.
 *
 * @param serializer [KSerializer]<[T]>
 * @param fileName File path in plugin data folder
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.ktConfig(serializer: KSerializer<T>, fileName: String, yaml: Yaml = Yaml.default): T? {
    return ktConfig(serializer, dataFolder.resolve(fileName), yaml)
}

/**
 * Load config.
 *
 * @param serializer [KSerializer]<[T]>
 * @param file File
 * @param default Default config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @return Config data or [default]
 */
fun <T : Any> ktConfig(serializer: KSerializer<T>, file: File, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer, file, yaml) ?: default.apply {
        file.parentFile?.mkdirs()
        file.writeText(Yaml.default.encodeToString(serializer, this))
    }
}

/**
 * Load config.
 *
 * @param serializer [KSerializer]<[T]>
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 */
fun <T : Any> JavaPlugin.ktConfig(serializer: KSerializer<T>, fileName: String, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer, dataFolder.resolve(fileName), default, yaml)
}

/**
 * Load config.
 *
 * @param file File
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @return Config data or null
 */
inline fun <reified T : Any> ktConfig(file: File, yaml: Yaml = Yaml.default): T? {
    return ktConfig(serializer(), file, yaml = yaml)
}

/**
 * Load config.
 *
 * @param fileName File path in plugin data folder
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @receiver [JavaPlugin]
 * @return Config data or null
 */
inline fun <reified T : Any> JavaPlugin.ktConfig(fileName: String, yaml: Yaml = Yaml.default): T? {
    return ktConfig(serializer(), fileName, yaml = yaml)
}

/**
 * Load config.
 *
 * @param file File
 * @param default Default config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @return Config data or [default]
 */
inline fun <reified T : Any> ktConfig(file: File, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer(), file, default, yaml)
}

/**
 * Load config.
 *
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 */
inline fun <reified T : Any> JavaPlugin.ktConfig(fileName: String, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer(), fileName, default, yaml)
}
