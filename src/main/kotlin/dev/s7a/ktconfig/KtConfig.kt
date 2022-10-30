package dev.s7a.ktconfig

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Load config.
 *
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfig(file: File): T? {
    val text = if (file.exists()) file.readText() else return null
    if (text.isBlank()) return null
    return KtConfigSerializer.deserialize(text)
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
inline fun <reified T : Any> JavaPlugin.ktConfig(fileName: String): T? {
    return ktConfig(dataFolder.resolve(fileName))
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
inline fun <reified T : Any> ktConfig(file: File, default: T): T {
    return ktConfig(file) ?: default.apply {
        saveKtConfig(file, this)
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
inline fun <reified T : Any> JavaPlugin.ktConfig(fileName: String, default: T): T {
    return ktConfig(dataFolder.resolve(fileName), default)
}

/**
 * Save config to file.
 *
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
fun <T : Any> saveKtConfig(file: File, content: T) {
    file.parentFile?.mkdirs()
    file.writeText(KtConfigSerializer.serialize(content))
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
fun <T : Any> JavaPlugin.saveKtConfig(fileName: String, content: T) {
    saveKtConfig(dataFolder.resolve(fileName), content)
}
