package dev.s7a.ktconfig

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Load config from [file].
 *
 * @param file Yaml file
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
    return KtConfigFile<T>(file, setting).load()
}

/**
 * Load config from [JavaPlugin.dataFolder]/[path].
 *
 * @param path File path in plugin data folder
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.ktConfigFile(
    path: String,
    setting: KtConfigSetting = KtConfigSetting(),
): T? {
    return KtConfigFile<T>(this, path, setting).load()
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file Yaml file
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
    noinline default: () -> T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return KtConfigFile<T>(file, default, setting).load()
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file Yaml file
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
    return KtConfigFile<T>(file, default, setting).load()
}

/**
 * Load config from [JavaPlugin.dataFolder]/[path]. If the file doesn't exist or is empty, save [default].
 *
 * @param path File path in plugin data folder
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
    path: String,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return KtConfigFile(this, path, default, setting).load()
}

/**
 * Save config to [file].
 *
 * @param file Yaml file
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
    KtConfigFile<T>(file, setting).save(content)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[path].
 *
 * @param path File path in plugin data folder
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
inline fun <reified T : Any> JavaPlugin.saveKtConfigFile(
    path: String,
    content: T,
    setting: KtConfigSetting = KtConfigSetting(),
) {
    KtConfigFile<T>(this, path, setting).save(content)
}

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigFile(
    file: File,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile<T> {
    return KtConfigFile(T::class, typeOf<T>(), file, setting)
}

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile<T> {
    return KtConfigFile(plugin.dataFolder.resolve(path), setting)
}

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    file: File,
    noinline default: () -> T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile.Default<T> {
    return KtConfigFile.Default(T::class, typeOf<T>(), file, default, setting)
}

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    file: File,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile.Default<T> {
    return KtConfigFile(file, { default }, setting)
}

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
    noinline default: () -> T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile.Default<T> {
    return KtConfigFile(plugin.dataFolder.resolve(path), default, setting)
}

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigFile.Default<T> {
    return KtConfigFile(plugin, path, { default }, setting)
}

/**
 * Handle config as [File].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file Yaml file
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
open class KtConfigFile<T : Any>(
    clazz: KClass<T>,
    type: KType,
    private val file: File,
    setting: KtConfigSetting = KtConfigSetting(),
) : KtConfig<T>(clazz, type, setting) {
    /**
     * Load config from [file].
     *
     * @return Config data or null
     * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
     * @throws dev.s7a.ktconfig.exception.TypeMismatchException
     * @since 1.0.0
     */
    open fun load(): T? {
        return if (file.exists()) loadFromString(file.readText()) else null
    }

    /**
     * Save config to [file].
     *
     * @param content Config data
     * @return Yaml data
     * @since 1.0.0
     */
    fun save(content: T) {
        file.parentFile?.mkdirs()
        file.writeText(saveToString(content))
    }

    /**
     * Handle config as [File].
     *
     * @param clazz [KClass]<[T]>
     * @param type [typeOf]<[T]>()
     * @param file Yaml file
     * @param default Default config data
     * @param setting Config setting
     * @param T Config type
     * @since 1.0.0
     */
    class Default<T : Any>(
        clazz: KClass<T>,
        type: KType,
        file: File,
        private val default: () -> T,
        setting: KtConfigSetting = KtConfigSetting(),
    ) : KtConfigFile<T>(clazz, type, file, setting) {
        /**
         * Load config from [file]. If [file] is empty, return [default].
         *
         * @return Config data or [default]
         * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
         * @throws dev.s7a.ktconfig.exception.TypeMismatchException
         * @since 1.0.0
         */
        override fun load(): T {
            return super.load() ?: default().apply(::save)
        }
    }
}
