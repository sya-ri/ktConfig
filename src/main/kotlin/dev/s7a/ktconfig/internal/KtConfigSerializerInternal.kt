package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KType

internal object KtConfigSerializerInternal {
    fun deserialize(type: KType, value: Any?): Any? {
        return when (type.classifier) {
            String::class -> value.toString()
            Int::class -> (value as? Number)?.toInt()
            UInt::class -> (value as? Number)?.toInt()?.toUInt()
            Boolean::class -> value as Boolean
            Double::class -> (value as? Number)?.toDouble()
            Float::class -> (value as? Number)?.toFloat()
            Long::class -> (value as? Number)?.toLong()
            ULong::class -> (value as? Number)?.toLong()?.toULong()
            Byte::class -> (value as? Number)?.toByte()
            UByte::class -> (value as? Number)?.toByte()?.toUByte()
            Char::class -> {
                when (value) {
                    is Char -> value
                    is String -> value.singleOrNull()
                    is Number -> value.toChar()
                    else -> null
                }
            }
            Short::class -> (value as? Number)?.toShort()
            UShort::class -> (value as? Number)?.toShort()?.toUShort()
            List::class -> {
                if (value !is List<*>) throw TypeMismatchException(type, value)
                val type0 = type.arguments[0].type!!
                value.map { deserialize(type0, it) }
            }
            Map::class -> {
                val entries = when (value) {
                    is ConfigurationSection -> value.getValues(false).entries
                    is Map<*, *> -> value.entries
                    else -> throw TypeMismatchException(type, value)
                }
                val type0 = type.arguments[0].type!!
                val type1 = type.arguments[1].type!!
                entries.mapNotNull { (key, value) ->
                    if (key == "null" && type0.isMarkedNullable) {
                        return@mapNotNull null to deserialize(type1, value)
                    }
                    deserializeKey(key.toString(), type0)?.let {
                        it to deserialize(type1, value)
                    }
                }.toMap()
            }
            else -> throw UnsupportedTypeException(type)
        }
    }

    private fun deserializeKey(key: String, type: KType): Any? {
        return when (type.classifier) {
            String::class -> key
            Int::class -> key.toIntOrNull()
            UInt::class -> key.toUIntOrNull()
            Boolean::class -> key.toBooleanStrictOrNull()
            // The path separator is '.', so it cannot be converted correctly.
            // Double::class -> keyString.toDoubleOrNull()
            // Float::class -> keyString.toFloatOrNull()
            Long::class -> key.toLongOrNull()
            ULong::class -> key.toULongOrNull()
            Byte::class -> key.toByteOrNull()
            UByte::class -> key.toUByteOrNull()
            Char::class -> key.singleOrNull()
            Short::class -> key.toShortOrNull()
            UShort::class -> key.toUShortOrNull()
            else -> throw UnsupportedTypeException(type)
        }
    }
}
