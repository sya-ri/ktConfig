package dev.s7a.ktconfig

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Load config from [text].
 *
 * @param text Yaml data
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(text: String): T? {
    return KtConfigString<T>().load(text)
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(
    text: String,
    noinline default: () -> T,
): T {
    return KtConfigString<T>(default).load(text)
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param text Yaml data
 * @param default Default config data
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 */
inline fun <reified T : Any> ktConfigString(
    text: String,
    default: T,
): T {
    return KtConfigString(default).load(text)
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
    return KtConfigString<T>().save(content)
}

/**
 * Handle config as [String].
 *
 * @param T Config type
 * @since 1.0.0
 */
inline fun <reified T : Any> KtConfigString(): KtConfigString<T> {
    return KtConfigString(T::class, typeOf<T>())
}

/**
 * Handle config as [String].
 *
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigString(noinline default: () -> T): KtConfigString.Default<T> {
    return KtConfigString.Default(T::class, typeOf<T>(), default)
}

/**
 * Handle config as [String].
 *
 * @param default Default config data
 * @param T Config type
 * @since 1.0.0
 */
@Suppress("FunctionName")
inline fun <reified T : Any> KtConfigString(default: T): KtConfigString.Default<T> {
    return KtConfigString({ default })
}

/**
 * Handle config as [String].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param T Config type
 * @since 1.0.0
 */
open class KtConfigString<T : Any>(
    clazz: KClass<T>,
    type: KType,
) : KtConfig<T>(clazz, type) {
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
     * @param T Config type
     * @since 1.0.0
     */
    class Default<T : Any>(
        clazz: KClass<T>,
        type: KType,
        private val default: () -> T,
    ) : KtConfigString<T>(clazz, type) {
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
