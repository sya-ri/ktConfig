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
        if (text.isBlank()) return null
        val section = ContentSerializer().section(clazz, type, ProjectionMap(clazz, type))
        val values =
            YamlConfiguration()
                .apply {
                    options().pathSeparator(PATH_SEPARATOR)
                    loadFromString(text)
                }.getValues(false)
        return section.deserialize(Deserializer(), "", values)
    }

    protected fun saveToString(content: T): String {
        val section = ContentSerializer().section(clazz, type, ProjectionMap(clazz, type))
        return YamlConfiguration()
            .apply {
                options().pathSeparator(PATH_SEPARATOR).setHeaderComment(clazz.findComment())
                setSection(null, section.serialize("", content))
            }.saveToString()
    }

    private fun YamlConfiguration.setSection(
        parent: String?,
        section: Section,
    ) {
        fun setValue(
            path: String,
            comments: List<String>?,
            value: Any?,
        ) {
            when (value) {
                is Section -> {
                    setSection(path, value)
                }
                is Map<*, *> -> {
                    value.entries.forEach { (k, v) ->
                        setValue("${path}${PATH_SEPARATOR}$k", null, v)
                    }
                }
                else -> {
                    set(path, value)
                }
            }
            setComment(path, comments)
        }

        section.values?.forEach { (name, comments, value) ->
            setValue(if (parent != null) "$parent$PATH_SEPARATOR$name" else name, comments, value)
        }
    }
}
