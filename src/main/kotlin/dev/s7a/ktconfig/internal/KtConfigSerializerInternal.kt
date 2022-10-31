package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

internal object KtConfigSerializerInternal {
    fun <T> KFunction<T>.callByValues(values: Map<String, Any?>): T? {
        return parameters.mapNotNull { parameter ->
            val value = values.get(parameter)
            if (value == Unit) {
                null
            } else {
                parameter to value
            }
        }.toMap().run(::callBy)
    }

    fun Map<String, Any?>.get(parameter: KParameter): Any? {
        val name = parameter.name!!
        val type = parameter.type
        val value = if (contains(name)) {
            deserialize(type, get(name))
        } else {
            if (parameter.isOptional) {
                // Use default value: Unit
            } else {
                null
            }
        }
        if (value == null && type.isMarkedNullable.not()) {
            throw TypeMismatchException(type, null)
        }
        return value
    }

    private fun deserialize(type: KType, value: Any?): Any? {
        return when (val classifier = type.classifier) {
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
            is KClass<*> -> {
                val constructor = classifier.primaryConstructor ?: throw UnsupportedTypeException(type)
                val values = when (value) {
                    is ConfigurationSection -> value.getValues(false)
                    is Map<*, *> -> value.entries.filterIsInstance<Map.Entry<String, Any?>>().associate { it.key to it.value }
                    else -> throw TypeMismatchException(type, value)
                }
                constructor.callByValues(values)
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
