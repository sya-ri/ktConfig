package dev.s7a.ktconfig

import dev.s7a.ktconfig.internal.KtConfigSerialization
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, text: String): T? {
    if (text.isBlank()) return null
    return KtConfigSerialization.deserialize(clazz, text)
}

/**
 * Load config.
 *
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String): T? {
    return ktConfigString(T::class, text)
}

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, text: String, default: T): T {
    return ktConfigString(clazz, text) ?: default
}

/**
 * Load config.
 *
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String, default: T): T {
    return ktConfigString(T::class, text, default)
}

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, file: File): T? {
    return if (file.exists()) ktConfigString(clazz, file.readText()) else null
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
    return ktConfigFile(T::class, file)
}

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param fileName File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, fileName: String): T? {
    return ktConfigFile(clazz, dataFolder.resolve(fileName))
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
    return ktConfigFile(T::class, fileName)
}

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param file File
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, file: File, default: T): T {
    return ktConfigFile(clazz, file) ?: default.apply {
        saveKtConfigFile(clazz, file, this)
    }
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
    return ktConfigFile(T::class, file, default)
}

/**
 * Load config.
 *
 * @param clazz [KClass]<[T]>
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, fileName: String, default: T): T {
    return ktConfigFile(clazz, dataFolder.resolve(fileName), default)
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
    return ktConfigFile(T::class, fileName, default)
}

/**
 * Save config to string.
 *
 * @param clazz [KClass]<[T]>
 * @param content Config data
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 */
fun <T : Any> saveKtConfigString(clazz: KClass<T>, content: T): String {
    return KtConfigSerialization.serialize(clazz, content)
}

/**
 * Save config to string.
 *
 * @param content Config data
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigString(content: T): String {
    return saveKtConfigString(T::class, content)
}

/**
 * Save config to file.
 *
 * @param clazz [KClass]<[T]>
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
fun <T : Any> saveKtConfigFile(clazz: KClass<T>, file: File, content: T) {
    file.parentFile?.mkdirs()
    file.writeText(saveKtConfigString(clazz, content))
}

/**
 * Save config to file.
 *
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigFile(file: File, content: T) {
    saveKtConfigFile(T::class, file, content)
}

/**
 * Save config to file.
 *
 * @param clazz [KClass]<[T]>
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
fun <T : Any> JavaPlugin.saveKtConfigFile(clazz: KClass<T>, fileName: String, content: T) {
    saveKtConfigFile(clazz, dataFolder.resolve(fileName), content)
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
inline fun <reified T : Any> JavaPlugin.saveKtConfigFile(fileName: String, content: T) {
    saveKtConfigFile(T::class, fileName, content)
}
