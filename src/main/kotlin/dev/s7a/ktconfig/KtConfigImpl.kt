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
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String): T? {
    if (text.isBlank()) return null
    return KtConfigSerialization.fromString(clazz, type, text)
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
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String, default: T): T {
    return ktConfigString(clazz, type, text) ?: default
}

/**
 * Load config from [file].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File): T? {
    return if (file.exists()) ktConfigString(clazz, type, file.readText()) else null
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
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String): T? {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName))
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
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File, default: T): T {
    return ktConfigFile(clazz, type, file) ?: default.apply {
        saveKtConfigFile(clazz, type, file, this)
    }
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
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String, default: T): T {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName), default)
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
