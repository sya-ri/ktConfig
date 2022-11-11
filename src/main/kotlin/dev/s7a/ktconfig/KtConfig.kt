package dev.s7a.ktconfig

import dev.s7a.ktconfig.internal.KtConfigSerialization
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Load config from [text].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String): T? {
    if (text.isBlank()) return null
    return KtConfigSerialization.fromString(clazz, type, text)
}

/**
 * Load config from [text].
 *
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String): T? {
    return ktConfigString(T::class, typeOf<T>(), text)
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String, default: T): T {
    return ktConfigString(clazz, type, text) ?: default
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String, default: T): T {
    return ktConfigString(T::class, typeOf<T>(), text, default)
}

/**
 * Load config from [file].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File): T? {
    return if (file.exists()) ktConfigString(clazz, type, file.readText()) else null
}

/**
 * Load config from [file].
 *
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(file: File): T? {
    return ktConfigFile(T::class, typeOf<T>(), file)
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param fileName File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String): T? {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName))
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName].
 *
 * @param fileName File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(fileName: String): T? {
    return ktConfigFile(T::class, typeOf<T>(), fileName)
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File, default: T): T {
    return ktConfigFile(clazz, type, file) ?: default.apply {
        saveKtConfigFile(clazz, type, file, this)
    }
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file File
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(file: File, default: T): T {
    return ktConfigFile(T::class, typeOf<T>(), file, default)
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName]. If the file doesn't exist or is empty, save [default].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String, default: T): T {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName), default)
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName]. If the file doesn't exist or is empty, save [default].
 *
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(fileName: String, default: T): T {
    return ktConfigFile(T::class, typeOf<T>(), fileName, default)
}

/**
 * Save config to string.
 *
 * @param clazz [KClass]<[T]>
 * @param type [KType] of [clazz]
 * @param content Config data
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> saveKtConfigString(clazz: KClass<T>, type: KType, content: T): String {
    return KtConfigSerialization.toString(clazz, type, content)
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
    return saveKtConfigString(T::class, typeOf<T>(), content)
}

/**
 * Save config to [file].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> saveKtConfigFile(clazz: KClass<T>, type: KType, file: File, content: T) {
    file.parentFile?.mkdirs()
    file.writeText(saveKtConfigString(clazz, type, content))
}

/**
 * Save config to [file].
 *
 * @param file File
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigFile(file: File, content: T) {
    saveKtConfigFile(T::class, typeOf<T>(), file, content)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[fileName].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.saveKtConfigFile(clazz: KClass<T>, type: KType, fileName: String, content: T) {
    saveKtConfigFile(clazz, type, dataFolder.resolve(fileName), content)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[fileName].
 *
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.saveKtConfigFile(fileName: String, content: T) {
    saveKtConfigFile(T::class, typeOf<T>(), fileName, content)
}
