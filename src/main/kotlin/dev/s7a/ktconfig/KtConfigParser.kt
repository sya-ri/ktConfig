package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.full.primaryConstructor

object KtConfigParser {
    inline fun <reified T : Any> fromString(text: String): T? {
        val constructor = T::class.primaryConstructor ?: return null
        val parameters = YamlConfiguration().run {
            loadFromString(text)
            constructor.parameters.associateWith { parameter ->
                val name = parameter.name!!
                val type = parameter.type
                when (type.classifier) {
                    else -> throw UnsupportedTypeException(type)
                }
            }
        }
        return constructor.callBy(parameters)
    }

    fun <T : Any> toString(content: T): String {
        TODO()
    }
}
