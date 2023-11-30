package dev.s7a.ktconfig

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.typeOf

/**
 * Load config from [text].
 *
 * @param text Yaml data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(
    text: String,
    setting: KtConfigSetting = KtConfigSetting(),
): T? {
    return ktConfigString(T::class, typeOf<T>(), text, setting)
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param text Yaml data
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(
    text: String,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return ktConfigString(T::class, typeOf<T>(), text, default, setting)
}

/**
 * Load config from [file].
 *
 * @param file File
 * @param setting Config setting
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    file: File,
    setting: KtConfigSetting = KtConfigSetting(),
): T? {
    return ktConfigFile(T::class, typeOf<T>(), file, setting)
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName].
 *
 * @param fileName File path in plugin data folder
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(
    fileName: String,
    setting: KtConfigSetting = KtConfigSetting(),
): T? {
    return ktConfigFile(T::class, typeOf<T>(), fileName, setting)
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file File
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    file: File,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return ktConfigFile(T::class, typeOf<T>(), file, default, setting)
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName]. If the file doesn't exist or is empty, save [default].
 *
 * @param fileName File path in plugin data folder
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(
    fileName: String,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return ktConfigFile(T::class, typeOf<T>(), fileName, default, setting)
}

/**
 * Save config to string.
 *
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigString(
    content: T,
    setting: KtConfigSetting = KtConfigSetting(),
): String {
    return saveKtConfigString(T::class, typeOf<T>(), content, setting)
}

/**
 * Save config to [file].
 *
 * @param file File
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigFile(
    file: File,
    content: T,
    setting: KtConfigSetting = KtConfigSetting(),
) {
    saveKtConfigFile(T::class, typeOf<T>(), file, content, setting)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[fileName].
 *
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.saveKtConfigFile(
    fileName: String,
    content: T,
    setting: KtConfigSetting = KtConfigSetting(),
) {
    saveKtConfigFile(T::class, typeOf<T>(), fileName, content, setting)
}
