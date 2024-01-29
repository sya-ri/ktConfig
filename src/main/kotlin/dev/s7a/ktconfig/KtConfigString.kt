package dev.s7a.ktconfig

import kotlin.reflect.KClass
import kotlin.reflect.KType
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
    return KtConfigString<T>(setting).load(text)
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
    noinline default: () -> T,
    setting: KtConfigSetting = KtConfigSetting(),
): T {
    return KtConfigString<T>(default, setting).load(text)
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
    return KtConfigString(default, setting).load(text)
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
    return KtConfigString<T>(setting).save(content)
}

/**
 * Handle config as [String].
 *
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigString(setting: KtConfigSetting = KtConfigSetting()): KtConfigString<T> {
    return KtConfigString(T::class, typeOf<T>(), setting)
}

/**
 * Handle config as [String].
 *
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigString(
    noinline default: () -> T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigString.Default<T> {
    return KtConfigString.Default(T::class, typeOf<T>(), default, setting)
}

/**
 * Handle config as [String].
 *
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigString(
    default: T,
    setting: KtConfigSetting = KtConfigSetting(),
): KtConfigString.Default<T> {
    return KtConfigString({ default }, setting)
}

/**
 * Handle config as [String].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 */
open class KtConfigString<T : Any>(
    clazz: KClass<T>,
    type: KType,
    setting: KtConfigSetting = KtConfigSetting(),
) : KtConfig<T>(clazz, type, setting) {
    /**
     * Load config from [text].
     *
     * @param text Yaml data
     * @return Config data or null
     * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
     * @throws dev.s7a.ktconfig.exception.TypeMismatchException
     * @since 1.0.0
     */
    open fun load(text: String): T? {
        return loadFromString(text)
    }

    /**
     * Save config to [String].
     *
     * @param content Config data
     * @return Yaml data
     * @since 1.0.0
     */
    fun save(content: T): String {
        return saveToString(content)
    }

    /**
     * Handle config as [String].
     *
     * @param clazz [KClass]<[T]>
     * @param type [typeOf]<[T]>()
     * @param default Default config data
     * @param setting Config setting
     * @param T Config type
     * @since 1.0.0
     */
    class Default<T : Any>(
        clazz: KClass<T>,
        type: KType,
        private val default: () -> T,
        setting: KtConfigSetting = KtConfigSetting(),
    ) : KtConfigString<T>(clazz, type, setting) {
        /**
         * Load config from [text]. If [text] is empty, return [default].
         *
         * @param text Yaml data
         * @return Config data or [default]
         * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
         * @throws dev.s7a.ktconfig.exception.TypeMismatchException
         * @since 1.0.0
         */
        override fun load(text: String): T {
            return super.load(text) ?: default()
        }
    }
}
