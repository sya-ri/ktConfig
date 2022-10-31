package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.internal.KtConfigSerializerInternal
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

object KtConfigSerializer {
    fun Map<String, Any?>.getValue(name: String, type: KType): Any? {
        return get(name)?.let {
            KtConfigSerializerInternal.deserialize(type, it)
        } ?: run {
            if (type.isMarkedNullable) {
                null
            } else {
                throw TypeMismatchException(type, null)
            }
        }
    }

    inline fun <reified T : Any> deserialize(text: String): T? {
        val constructor = T::class.primaryConstructor ?: return null
        val parameters = YamlConfiguration().run {
            loadFromString(text)
            val values = getValues(false)
            constructor.parameters.associateWith { parameter ->
                values.getValue(parameter.name!!, parameter.type)
            }
        }
        return constructor.callBy(parameters)
    }

    fun <T : Any> serialize(value: T): String {
        TODO()
    }
}
