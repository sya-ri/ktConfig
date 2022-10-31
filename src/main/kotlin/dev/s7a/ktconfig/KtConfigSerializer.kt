package dev.s7a.ktconfig

import dev.s7a.ktconfig.internal.KtConfigSerializerInternal.callByValues
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object KtConfigSerializer {
    fun <T : Any> deserialize(clazz: KClass<T>, text: String): T? {
        val constructor = clazz.primaryConstructor ?: return null
        val values = YamlConfiguration().apply { loadFromString(text) }.getValues(false)
        return constructor.callByValues(values)
    }

    inline fun <reified T : Any> deserialize(text: String): T? {
        return deserialize(T::class, text)
    }

    fun <T : Any> serialize(value: T): String {
        TODO()
    }
}
