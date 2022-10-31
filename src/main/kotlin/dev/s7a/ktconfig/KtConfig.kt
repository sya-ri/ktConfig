package dev.s7a.ktconfig

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Load config.
 *
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String): T? {
    if (text.isBlank()) return null
    return KtConfigSerializer.deserialize(text)
}

/**
 * Load config.
 *
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(file: File): T? {
    return if (file.exists()) ktConfigString(file.readText()) else null
}

/**
 * Load config.
 *
 * @param fileName File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(fileName: String): T? {
    return ktConfigFile(dataFolder.resolve(fileName))
}

/**
 * Load config.
 *
 * @param file File
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(file: File, default: T): T {
    return ktConfigFile(file) ?: default.apply {
        saveKtConfigFile(file, this)
    }
}

/**
 * Load config.
 *
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(fileName: String, default: T): T {
    return ktConfigFile(dataFolder.resolve(fileName), default)
}

/**
 * Save config to string.
 *
 * @param content Config data
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 */
fun <T : Any> saveKtConfigString(content: T): String {
    return KtConfigSerializer.serialize(content)
}

/**
 * Save config to file.
 *
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
fun <T : Any> saveKtConfigFile(file: File, content: T) {
    file.parentFile?.mkdirs()
    file.writeText(saveKtConfigString(content))
}

/**
 * Save config to file.
 *
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.saveKtConfigFile(fileName: String, content: T) {
    saveKtConfigFile(dataFolder.resolve(fileName), content)
}
