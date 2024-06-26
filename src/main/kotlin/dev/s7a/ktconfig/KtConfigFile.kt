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
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(file: File): T? = KtConfigFile<T>(file).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path].
 *
 * @param plugin JavaPlugin
 * @param path File path in plugin data folder
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    plugin: JavaPlugin,
    path: String,
): T? = KtConfigFile<T>(plugin, path).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path].
 *
 * @param path File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
@JvmName("ktConfigFile_")
inline fun <reified T : Any> JavaPlugin.ktConfigFile(path: String): T? = KtConfigFile<T>(this, path).load()

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    file: File,
    noinline default: () -> T,
): T = KtConfigFile<T>(file, default).load()

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    file: File,
    default: T,
): T = KtConfigFile<T>(file, default).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path]. If the file doesn't exist or is empty, save [default].
 *
 * @param plugin JavaPlugin
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    plugin: JavaPlugin,
    path: String,
    noinline default: () -> T,
): T = KtConfigFile(plugin, path, default).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path]. If the file doesn't exist or is empty, save [default].
 *
 * @param plugin JavaPlugin
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigFile(
    plugin: JavaPlugin,
    path: String,
    default: T,
): T = KtConfigFile(plugin, path, default).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path]. If the file doesn't exist or is empty, save [default].
 *
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
@JvmName("ktConfigFile_")
inline fun <reified T : Any> JavaPlugin.ktConfigFile(
    path: String,
    noinline default: () -> T,
): T = KtConfigFile(this, path, default).load()

/**
 * Load config from [JavaPlugin.dataFolder]/[path]. If the file doesn't exist or is empty, save [default].
 *
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
@JvmName("ktConfigFile_")
inline fun <reified T : Any> JavaPlugin.ktConfigFile(
    path: String,
    default: T,
): T = KtConfigFile(this, path, default).load()

/**
 * Save config to [file].
 *
 * @param file Yaml file
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigFile(
    file: File,
    content: T,
) {
    KtConfigFile<T>(file).save(content)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[path].
 *
 * @param plugin JavaPlugin
 * @param path File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> saveKtConfigFile(
    plugin: JavaPlugin,
    path: String,
    content: T,
) {
    KtConfigFile<T>(plugin, path).save(content)
}

/**
 * Save config to [JavaPlugin.dataFolder]/[path].
 *
 * @param path File path in plugin data folder
 * @param content Config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
@JvmName("saveKtConfigFile_")
inline fun <reified T : Any> JavaPlugin.saveKtConfigFile(
    path: String,
    content: T,
) {
    KtConfigFile<T>(this, path).save(content)
}

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigFile(file: File): KtConfigFile<T> = KtConfigFile(T::class, typeOf<T>(), file)

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
): KtConfigFile<T> = KtConfigFile(plugin.dataFolder.resolve(path))

/**
 * Handle config as [File].
 *
 * @param path File path in plugin data folder
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
@JvmName("KtConfigFile_")
inline fun <reified T : Any> JavaPlugin.KtConfigFile(path: String): KtConfigFile<T> = KtConfigFile(dataFolder.resolve(path))

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    file: File,
    noinline default: () -> T,
): KtConfigFile.Default<T> = KtConfigFile.Default(T::class, typeOf<T>(), file, default)

/**
 * Handle config as [File].
 *
 * @param file Yaml file
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    file: File,
    default: T,
): KtConfigFile.Default<T> = KtConfigFile(file) { default }

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
    noinline default: () -> T,
): KtConfigFile.Default<T> = KtConfigFile(plugin.dataFolder.resolve(path), default)

/**
 * Handle config as [File].
 *
 * @param plugin [JavaPlugin]
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigFile(
    plugin: JavaPlugin,
    path: String,
    default: T,
): KtConfigFile.Default<T> = KtConfigFile(plugin, path) { default }

/**
 * Handle config as [File].
 *
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
@Suppress("FunctionName")
@JvmName("KtConfigFile_")
inline fun <reified T : Any> JavaPlugin.KtConfigFile(
    path: String,
    noinline default: () -> T,
): KtConfigFile.Default<T> = KtConfigFile(dataFolder.resolve(path), default)

/**
 * Handle config as [File].
 *
 * @param path File path in plugin data folder
 * @param default Default config data
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 */
@Suppress("FunctionName")
@JvmName("KtConfigFile_")
inline fun <reified T : Any> JavaPlugin.KtConfigFile(
    path: String,
    default: T,
): KtConfigFile.Default<T> = KtConfigFile(this, path) { default }

/**
 * Handle config as [File].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file Yaml file
 * @param T Config type
 * @since 1.0.0
 */
open class KtConfigFile<T : Any>(
    clazz: KClass<T>,
    type: KType,
    private val file: File,
) : KtConfig<T>(clazz, type) {
    /**
     * Load config from [file].
     *
     * @return Config data or null
     * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
     * @throws dev.s7a.ktconfig.exception.TypeMismatchException
     * @since 1.0.0
     */
    open fun load(): T? = if (file.exists()) loadFromString(file.readText()) else null

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
     * @param T Config type
     * @since 1.0.0
     */
    class Default<T : Any>(
        clazz: KClass<T>,
        type: KType,
        file: File,
        private val default: () -> T,
    ) : KtConfigFile<T>(clazz, type, file) {
        /**
         * Load config from [file]. If [file] is empty, return [default].
         *
         * @return Config data or [default]
         * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
         * @throws dev.s7a.ktconfig.exception.TypeMismatchException
         * @since 1.0.0
         */
        override fun load(): T = super.load() ?: default().apply(::save)
    }
}
