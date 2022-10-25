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
 * @since 1.0.0
 */
fun <T : Any> ktConfig(serializer: KSerializer<T>, file: File, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer, file, yaml) ?: default.apply {
        saveKtConfig(serializer, file, this, yaml)
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
 * @since 1.0.0
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
 * @since 1.0.0
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
 * @since 1.0.0
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
 * @since 1.0.0
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
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfig(fileName: String, default: T, yaml: Yaml = Yaml.default): T {
    return ktConfig(serializer(), fileName, default, yaml)
}

/**
 * Save config to file.
 *
 * @param serializer [KSerializer]<[T]>
 * @param file File
 * @param content Config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @since 1.0.0
 */
fun <T : Any> saveKtConfig(serializer: KSerializer<T>, file: File, content: T, yaml: Yaml = Yaml.default) {
    file.parentFile?.mkdirs()
    file.writeText(yaml.encodeToString(serializer, content))
}

/**
 * Save config to file.
 *
 * @param serializer [KSerializer]<[T]>
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.saveKtConfig(serializer: KSerializer<T>, fileName: String, content: T, yaml: Yaml = Yaml.default) {
    saveKtConfig(serializer, dataFolder.resolve(fileName), content, yaml)
}

/**
 * Save config to file.
 *
 * @param file File
 * @param content Config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfig(file: File, content: T, yaml: Yaml = Yaml.default) {
    saveKtConfig(serializer(), file, content, yaml)
}

/**
 * Save config to file.
 *
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param yaml Yaml used for parse (default: `Yaml.default`)
 * @param T Config type marked @Serializable
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.saveKtConfig(fileName: String, content: T, yaml: Yaml = Yaml.default) {
    saveKtConfig(serializer(), fileName, content, yaml)
}
