package dev.s7a.ktconfig

import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.full.primaryConstructor

object KtConfigSerializer {
    inline fun <reified T : Any> deserialize(text: String): T? {
        val constructor = T::class.primaryConstructor ?: return null
        val parameters = YamlConfiguration().run {
            loadFromString(text)
            constructor.parameters.associateWith { parameter ->
                getFromType(parameter.name!!, parameter.type)
            }
        }
        return constructor.callBy(parameters)
    }

    fun <T : Any> serialize(value: T): String {
        TODO()
    }
}
