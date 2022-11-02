package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

internal object KtConfigSerialization {
    fun <T : Any> deserialize(clazz: KClass<T>, text: String): T? {
        val constructor = clazz.primaryConstructor ?: return null
        val values = YamlConfiguration().apply { loadFromString(text) }.getValues(false)
        return constructor.callByValues(values)
    }

    fun <T : Any> serialize(clazz: KClass<T>, value: T): String {
        return YamlConfiguration().apply {
            clazz.memberProperties.forEach {
                set(it.name, serialize(it.returnType, it.get(value)))
            }
        }.saveToString()
    }

    private fun <T> KFunction<T>.callByValues(values: Map<String, Any?>): T? {
        return parameters.mapNotNull { parameter ->
            val value = values.get(parameter)
            if (value == Unit) {
                null
            } else {
                parameter to value
            }
        }.toMap().run(::callBy)
    }

    private fun Map<String, Any?>.get(parameter: KParameter): Any? {
        val name = parameter.name!!
        val type = parameter.type
        val value = when {
            contains(name) -> deserialize(type, get(name))
            parameter.isOptional -> {} // Use default value: Unit
            else -> null
        }
        if (value == null && type.isMarkedNullable.not()) {
            throw TypeMismatchException(type, null)
        }
        return value
    }

    private fun deserialize(type: KType, value: Any?): Any? {
        return when (val classifier = type.classifier) {
            String::class -> value.toString()
            Int::class -> {
                when (value) {
                    is Number -> value.toInt()
                    is String -> value.toIntOrNull()
                    else -> null
                }
            }
            UInt::class -> {
                when (value) {
                    is Number -> value.toInt().toUInt()
                    is String -> value.toUIntOrNull()
                    else -> null
                }
            }
            Boolean::class -> {
                when (value) {
                    is Boolean -> value
                    is String -> value.toBooleanStrictOrNull()
                    else -> null
                }
            }
            Double::class -> {
                when (value) {
                    is Number -> value.toDouble()
                    is String -> value.toDoubleOrNull()
                    else -> null
                }
            }
            Float::class -> {
                when (value) {
                    is Number -> value.toFloat()
                    is String -> value.toFloatOrNull()
                    else -> null
                }
            }
            Long::class -> {
                when (value) {
                    is Number -> value.toLong()
                    is String -> value.toLongOrNull()
                    else -> null
                }
            }
            ULong::class -> {
                when (value) {
                    is Number -> value.toLong().toULong()
                    is String -> value.toULongOrNull()
                    else -> null
                }
            }
            Byte::class -> {
                when (value) {
                    is Number -> value.toByte()
                    is String -> value.toByteOrNull()
                    else -> null
                }
            }
            UByte::class -> {
                when (value) {
                    is Number -> value.toByte().toUByte()
                    is String -> value.toUByteOrNull()
                    else -> null
                }
            }
            Char::class -> {
                when (value) {
                    is Char -> value
                    is String -> value.singleOrNull()
                    is Number -> value.toChar()
                    else -> null
                }
            }
            Short::class -> {
                when (value) {
                    is Number -> value.toShort()
                    is String -> value.toShortOrNull()
                    else -> null
                }
            }
            UShort::class -> {
                when (value) {
                    is Number -> value.toShort().toUShort()
                    is String -> value.toUShortOrNull()
                    else -> null
                }
            }
            UUID::class -> runCatching { UUID.fromString(value.toString()) }.getOrNull()
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = type.arguments[0].type!!
                when (value) {
                    is List<*> -> {
                        value.map { deserialize(type0, it) }.run {
                            when (classifier) {
                                Set::class -> toSet()
                                HashSet::class -> toHashSet()
                                LinkedHashSet::class -> LinkedHashSet(this)
                                else -> this
                            }
                        }
                    }
                    else -> {
                        listOf(deserialize(type0, value))
                    }
                }
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
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
                    deserializeKey(type0, key.toString())?.let {
                        it to deserialize(type1, value)
                    }
                }.toMap()
            }
            is KClass<*> -> {
                when {
                    classifier.isSubclassOf(ConfigurationSerializable::class) -> {
                        value
                    }
                    classifier.isSubclassOf(Enum::class) -> {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            java.lang.Enum.valueOf(classifier.java as Class<out Enum<*>>, value.toString())
                        } catch (ex: IllegalArgumentException) {
                            null
                        }
                    }
                    else -> {
                        val constructor = classifier.primaryConstructor ?: throw UnsupportedTypeException(type, "value")
                        val values = when (value) {
                            is ConfigurationSection -> value.getValues(false)
                            is Map<*, *> -> value.entries.filterIsInstance<Map.Entry<String, Any?>>().associate { it.key to it.value }
                            else -> throw TypeMismatchException(type, value)
                        }
                        constructor.callByValues(values)
                    }
                }
            }
            else -> throw UnsupportedTypeException(type, "value")
        }
    }

    private fun deserializeKey(type: KType, key: String): Any? {
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
            else -> throw UnsupportedTypeException(type, "key")
        }
    }

    private fun serialize(type: KType, value: Any?): Any? {
        if (value == null) return null
        return when (val classifier = type.classifier) {
            String::class -> value
            Int::class -> value
            UInt::class -> (value as UInt).toLong()
            Boolean::class -> value
            Double::class -> value
            Float::class -> value
            Long::class -> value
            ULong::class -> (value as ULong).toLong().takeUnless { it < 0 } ?: value.toString()
            Byte::class -> value
            UByte::class -> (value as UByte).toShort()
            Char::class -> value
            Short::class -> value
            UShort::class -> (value as UShort).toInt()
            UUID::class -> value.toString()
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = type.arguments[0].type!!
                (value as Iterable<*>).map { serialize(type0, it) }
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
                val type0 = type.arguments[0].type!!
                val type1 = type.arguments[1].type!!
                (value as Map<*, *>).map { serializeKey(type0, it.key.toString()) to serialize(type1, it.value) }.toMap()
            }
            is KClass<*> -> {
                when {
                    classifier.isSubclassOf(ConfigurationSerializable::class) -> {
                        value
                    }
                    classifier.isSubclassOf(Enum::class) -> {
                        try {
                            (value as Enum<*>).name
                        } catch (ex: IllegalArgumentException) {
                            null
                        }
                    }
                    else -> {
                        classifier.memberProperties.associate {
                            it.name to serialize(it.returnType, it.call(value))
                        }
                    }
                }
            }
            else -> value
        }
    }

    private fun serializeKey(type: KType, key: String): Any? {
        return when (type.classifier) {
            String::class -> key
            Int::class -> key
            UInt::class -> key.toLong()
            Boolean::class -> key.toBooleanStrictOrNull()
            // The path separator is '.', so it cannot be converted correctly.
            // Double::class -> keyString.toDoubleOrNull()
            // Float::class -> keyString.toFloatOrNull()
            Long::class -> key
            ULong::class -> key.toLongOrNull()?.takeUnless { it < 0 } ?: key
            Byte::class -> key
            UByte::class -> key.toShort()
            Char::class -> key
            Short::class -> key
            UShort::class -> key.toInt()
            else -> throw UnsupportedTypeException(type, "key")
        }
    }
}
