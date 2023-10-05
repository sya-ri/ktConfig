package dev.s7a.ktconfig

import dev.s7a.ktconfig.internal.ContentSerializer
import dev.s7a.ktconfig.internal.Deserializer
import dev.s7a.ktconfig.internal.ProjectionMap
import dev.s7a.ktconfig.internal.Section
import dev.s7a.ktconfig.internal.findComment
import dev.s7a.ktconfig.internal.reflection.YamlConfigurationOptionsReflection.setComment
import dev.s7a.ktconfig.internal.reflection.YamlConfigurationOptionsReflection.setHeaderComment
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Change the path separator to be able to use Double or Float as key
 */
private const val pathSeparator = 0x00.toChar()

/**
 * Load config from [text].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param text Yaml data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String, setting: KtConfigSetting = KtConfigSetting()): T? {
    val section = ContentSerializer(setting).section(clazz, type, ProjectionMap(clazz, type))
    if (text.isBlank()) return null
    val values = YamlConfiguration().apply {
        options().pathSeparator(pathSeparator)
        loadFromString(text)
    }.getValues(false)
    return section.deserialize(Deserializer(setting), "", values)
}

/**
 * Load config from [text]. If [text] is empty, return [default].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param text Yaml data
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigString(clazz: KClass<T>, type: KType, text: String, default: T, setting: KtConfigSetting = KtConfigSetting()): T {
    return ktConfigString(clazz, type, text, setting) ?: default
}

/**
 * Load config from [file].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param setting Config setting
 * @param T Config type
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File, setting: KtConfigSetting = KtConfigSetting()): T? {
    return if (file.exists()) ktConfigString(clazz, type, file.readText(), setting) else null
}

/**
 * Load config from [JavaPlugin.dataFolder]/[fileName].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param fileName File path in plugin data folder
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or null
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String, setting: KtConfigSetting = KtConfigSetting()): T? {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName), setting)
}

/**
 * Load config from [file]. If [file] doesn't exist or is empty, save [default].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param default Default config data
 * @param setting Config setting
 * @param T Config type
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> ktConfigFile(clazz: KClass<T>, type: KType, file: File, default: T, setting: KtConfigSetting = KtConfigSetting()): T {
    return ktConfigFile(clazz, type, file, setting) ?: default.apply {
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
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @return Config data or [default]
 * @throws dev.s7a.ktconfig.exception.UnsupportedTypeException
 * @throws dev.s7a.ktconfig.exception.TypeMismatchException
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.ktConfigFile(clazz: KClass<T>, type: KType, fileName: String, default: T, setting: KtConfigSetting = KtConfigSetting()): T {
    return ktConfigFile(clazz, type, dataFolder.resolve(fileName), default, setting)
}

/**
 * Save config to string.
 *
 * @param clazz [KClass]<[T]>
 * @param type [KType] of [clazz]
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @return Yaml data
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> saveKtConfigString(clazz: KClass<T>, type: KType, content: T, setting: KtConfigSetting = KtConfigSetting()): String {
    val section = ContentSerializer(setting).section(clazz, type, ProjectionMap(clazz, type))
    return YamlConfiguration().apply {
        options().pathSeparator(pathSeparator).setHeaderComment(clazz.findComment())

        fun setSection(parent: String?, section: Section) {
            section.values?.forEach { name, (comments, value) ->
                val path = if (parent != null) "$parent$pathSeparator$name" else name
                when (value) {
                    is Section -> {
                        setSection(path, value)
                    }
                    is Map<*, *> -> {
                        fun map(map: Map<*, *>): Map<*, *> {
                            return map.entries.associate { (p, v) ->
                                p to when (v) {
                                    is Section -> {
                                        v.values?.entries?.associate {
                                            it.key to it.value.value
                                        }
                                    }
                                    is Map<*, *> -> {
                                        map(v)
                                    }
                                    else -> {
                                        v
                                    }
                                }
                            }
                        }

                        set(path, map(value))
                    }
                    else -> {
                        set(path, value)
                    }
                }
                setComment(path, comments)
            }
        }

        setSection(null, section.serialize("", content))
    }.saveToString()
}

/**
 * Save config to [file].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param file File
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> saveKtConfigFile(clazz: KClass<T>, type: KType, file: File, content: T, setting: KtConfigSetting = KtConfigSetting()) {
    file.parentFile?.mkdirs()
    file.writeText(saveKtConfigString(clazz, type, content, setting))
}

/**
 * Save config to [JavaPlugin.dataFolder]/[fileName].
 *
 * @param clazz [KClass]<[T]>
 * @param type [typeOf]<[T]>()
 * @param fileName File path in plugin data folder
 * @param content Config data
 * @param setting Config setting
 * @param T Config type
 * @receiver [JavaPlugin]
 * @since 1.0.0
 * @suppress
 */
fun <T : Any> JavaPlugin.saveKtConfigFile(clazz: KClass<T>, type: KType, fileName: String, content: T, setting: KtConfigSetting = KtConfigSetting()) {
    saveKtConfigFile(clazz, type, dataFolder.resolve(fileName), content, setting)
}
