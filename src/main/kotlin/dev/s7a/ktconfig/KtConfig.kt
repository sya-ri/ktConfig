package dev.s7a.ktconfig

import dev.s7a.ktconfig.internal.ContentSerializer
import dev.s7a.ktconfig.internal.Deserializer
import dev.s7a.ktconfig.internal.ProjectionMap
import dev.s7a.ktconfig.internal.Section
import dev.s7a.ktconfig.internal.findComment
import dev.s7a.ktconfig.internal.reflection.YamlConfigurationOptionsReflection.setComment
import dev.s7a.ktconfig.internal.reflection.YamlConfigurationOptionsReflection.setHeaderComment
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.KClass
import kotlin.reflect.KType

abstract class KtConfig<T : Any>(
    private val clazz: KClass<T>,
    private val type: KType,
) {
    companion object {
        /**
         * Change the path separator to be able to use Double or Float as key
         */
        private const val PATH_SEPARATOR = 0x00.toChar()
    }

    protected fun loadFromString(text: String): T? {
        val section = ContentSerializer().section(clazz, type, ProjectionMap(clazz, type))
        if (text.isBlank()) return null
        val values =
            YamlConfiguration().apply {
                options().pathSeparator(PATH_SEPARATOR)
                loadFromString(text)
            }.getValues(false)
        return section.deserialize(Deserializer(), "", values)
    }

    protected fun saveToString(content: T): String {
        val section = ContentSerializer().section(clazz, type, ProjectionMap(clazz, type))
        return YamlConfiguration().apply {
            options().pathSeparator(PATH_SEPARATOR).setHeaderComment(clazz.findComment())
            setSection(null, section.serialize("", content))
        }.saveToString()
    }

    private fun YamlConfiguration.setSection(
        parent: String?,
        section: Section,
    ) {
        section.values?.forEach { name, (comments, value) ->
            val path = if (parent != null) "$parent$PATH_SEPARATOR$name" else name
            when (value) {
                is Section -> {
                    setSection(path, value)
                }
                is Map<*, *> -> {
                    fun map(map: Map<*, *>): Map<*, *> {
                        return map.entries.associate { (p, v) ->
                            p to
                                when (v) {
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
}
