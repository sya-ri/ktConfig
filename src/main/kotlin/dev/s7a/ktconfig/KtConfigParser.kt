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
                    String::class -> getString(name)
                    Int::class -> getInt(name)
                    Boolean::class -> getBoolean(name)
                    Double::class -> getDouble(name)
                    Float::class -> getFloat(name)
                    Long::class -> getLong(name)
                    Byte::class -> getByte(name)
                    Char::class -> getChar(name)
                    Short::class -> getShort(name)
                    List::class -> {
                        when (type.arguments.first().type?.classifier) {
                            String::class -> getStringList(name)
                            Int::class -> getIntegerList(name)
                            Boolean::class -> getBooleanList(name)
                            Double::class -> getDoubleList(name)
                            Float::class -> getFloatList(name)
                            Long::class -> getLongList(name)
                            Byte::class -> getByteList(name)
                            Char::class -> getCharacterList(name)
                            Short::class -> getShortList(name)
                            else -> throw UnsupportedTypeException(type)
                        }
                    }
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
