package dev.s7a.ktconfig

import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KType

fun ConfigurationSection.getFloat(path: String): Float {
    val value = get(path)
    return if (value is Number) value.toFloat() else 0F
}

fun ConfigurationSection.getChar(path: String): Char {
    return when (val value = get(path)) {
        is Char -> value
        is String -> value.singleOrNull() ?: 0.toChar()
        is Int -> value.toChar()
        else -> 0.toChar()
    }
}

fun ConfigurationSection.getByte(path: String): Byte {
    return when (val value = get(path)) {
        is Byte -> value
        is String -> value.toByteOrNull() ?: 0.toByte()
        is Char -> value.code.toByte()
        is Number -> value.toByte()
        else -> 0.toByte()
    }
}

fun ConfigurationSection.getShort(path: String): Short {
    val value = get(path)
    return if (value is Number) value.toShort() else 0
}

private fun parseKey(keyString: String, type: KType): Pair<String, Any?>? {
    if (keyString == "null" && type.isMarkedNullable) {
        return keyString to null
    }
    return when (type.classifier) {
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
}

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
        UByte::class -> getByte(path).toUByte()
        Char::class -> getChar(path)
        Short::class -> getShort(path)
        UShort::class -> getShort(path).toUShort()
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
            getConfigurationSection(path)?.getKeys(false)?.mapNotNull { keyString ->
                parseKey(keyString, arguments[0].type!!)
            }?.associate { (keyString, key) ->
                key to getFromType("$path.$keyString", arguments[1].type!!)
            }
        }
        else -> throw UnsupportedTypeException(type)
    }
}
