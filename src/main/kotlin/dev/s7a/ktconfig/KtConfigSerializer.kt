package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

object KtConfigSerializer {
    fun ConfigurationSection.getFromType(path: String, type: KType): Any? {
        if (contains(path).not()) return null
        val arguments = type.arguments
        return when (type.classifier) {
            String::class -> getString(path)
            Int::class -> getInt(path)
            UInt::class -> getInt(path).toUInt()
            Boolean::class -> getBoolean(path)
            Double::class -> getDouble(path)
            Float::class -> getFloat(path)
            Long::class -> getLong(path)
            ULong::class -> getLong(path).toULong()
            Byte::class -> getByte(path)
            UByte::class -> getByte(path)?.toUByte()
            Char::class -> getChar(path)
            Short::class -> getShort(path)
            UShort::class -> getShort(path)?.toUShort()
            List::class -> {
                when (arguments[0].type?.classifier) {
                    String::class -> getStringList(path)
                    Int::class -> getIntegerList(path)
                    UInt::class -> getIntegerList(path).map(Int::toUInt)
                    Boolean::class -> getBooleanList(path)
                    Double::class -> getDoubleList(path)
                    Float::class -> getFloatList(path)
                    Long::class -> getLongList(path)
                    ULong::class -> getLongList(path).map(Long::toULong)
                    Byte::class -> getByteList(path)
                    UByte::class -> getByteList(path).map(Byte::toUByte)
                    Char::class -> getCharacterList(path)
                    Short::class -> getShortList(path)
                    UShort::class -> getShortList(path).map(Short::toUShort)
                    else -> throw UnsupportedTypeException(type)
                }
            }
            Map::class -> {
                val type0 = arguments[0].type!!
                getConfigurationSection(path)?.getKeys(false)?.mapNotNull { keyString ->
                    if (keyString == "null" && type0.isMarkedNullable) {
                        return@mapNotNull keyString to null
                    }
                    when (arguments[0].type?.classifier) {
                        String::class -> keyString
                        Int::class -> keyString.toIntOrNull()
                        UInt::class -> keyString.toUIntOrNull()
                        Boolean::class -> keyString.toBooleanStrictOrNull()
                        // The path separator is '.', so it cannot be converted correctly.
                        // Double::class -> keyString.toDoubleOrNull()
                        // Float::class -> keyString.toFloatOrNull()
                        Long::class -> keyString.toLongOrNull()
                        ULong::class -> keyString.toULongOrNull()
                        Byte::class -> keyString.toByteOrNull()
                        UByte::class -> keyString.toUByteOrNull()
                        Char::class -> keyString.singleOrNull()
                        Short::class -> keyString.toShortOrNull()
                        UShort::class -> keyString.toUShortOrNull()
                        else -> throw UnsupportedTypeException(type)
                    }?.let {
                        keyString to it
                    }
                }?.associate { (keyString, key) ->
                    key to getFromType("$path.$keyString", arguments[1].type!!)
                }
            }
            else -> throw UnsupportedTypeException(type)
        }
    }

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
