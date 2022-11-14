package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setComment
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setHeaderComment
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal object KtConfigSerialization {
    /**
     * Change the path separator to be able to use Double or Float as key
     */
    private const val pathSeparator = 0x00.toChar()

    fun <T : Any> fromString(clazz: KClass<T>, type: KType, text: String): T? {
        val constructor = clazz.primaryConstructor ?: return null
        val values = YamlConfiguration().apply {
            options().pathSeparator(pathSeparator)
            loadFromString(text)
        }.getValues(false)
        return constructor.callByValues(projectionMap(clazz, type), values)
    }

    fun <T : Any> toString(clazz: KClass<T>, type: KType, value: T): String {
        return YamlConfiguration().apply {
            options().pathSeparator(pathSeparator).setHeaderComment(clazz.findComment())
            set(clazz, type, value)
        }.saveToString()
    }

    private fun projectionMap(clazz: KClass<*>, type: KType): Map<KTypeParameter, KTypeProjection> {
        return clazz.typeParameters.mapIndexed { index, parameter ->
            parameter to type.arguments[index]
        }.toMap()
    }

    private fun KAnnotatedElement.findComment(): List<String>? {
        return findAnnotation<Comment>()?.lines?.toList()
    }

    private fun <T> KFunction<T>.callByValues(projectionMap: Map<KTypeParameter, KTypeProjection>, values: Map<String, Any?>): T? {
        isAccessible = true
        return parameters.mapNotNull { parameter ->
            val value = values.get(projectionMap, parameter)
            if (value == Unit) {
                null
            } else {
                parameter to value
            }
        }.toMap().run(::callBy)
    }

    private fun Map<String, Any?>.get(projectionMap: Map<KTypeParameter, KTypeProjection>, parameter: KParameter): Any? {
        val name = parameter.name!!
        val type = parameter.type
        val actualType = projectionMap[type.classifier]?.type ?: type
        val value = when {
            contains(name) -> deserialize(projectionMap, actualType, get(name))
            parameter.isOptional -> {} // Use default value: Unit
            else -> null
        }
        if (value == null && actualType.isMarkedNullable.not()) {
            throw TypeMismatchException(type, null)
        }
        return value
    }

    private fun ConfigurationSection.set(clazz: KClass<*>, type: KType, value: Any) {
        clazz.memberProperties.forEach {
            if (it.javaField == null) {
                // Ignore custom getters
                // https://discuss.kotlinlang.org/t/reflection-and-properties-checking-for-custom-getters-setters/22457/2
                return@forEach
            }
            it.isAccessible = true
            serialize(projectionMap(clazz, type), createSection(it.name), it.returnType, it.call(value)).run {
                if (this !is Unit) {
                    // Unit is that should be ignored
                    set(it.name, this)
                }
            }
            setComment(it.name, it.findComment())
        }
    }

    private fun deserialize(projectionMap: Map<KTypeParameter, KTypeProjection>, type: KType, value: Any?): Any? {
        return when (val classifier = type.classifier) {
            String::class -> value.toString()
            Int::class -> {
                when (value) {
                    is Number -> value.toInt()
                    is String -> runCatching { BigInteger(value).toInt() }.getOrNull()
                    else -> null
                }
            }
            UInt::class -> {
                when (value) {
                    is Number -> value.toInt().toUInt()
                    is String -> runCatching { BigInteger(value).toInt().toUInt() }.getOrNull()
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
                    is String -> runCatching { BigInteger(value).toLong() }.getOrNull()
                    else -> null
                }
            }
            ULong::class -> {
                when (value) {
                    is Number -> value.toLong().toULong()
                    is String -> runCatching { BigInteger(value).toLong().toULong() }.getOrNull()
                    else -> null
                }
            }
            Byte::class -> {
                when (value) {
                    is Number -> value.toByte()
                    is String -> runCatching { BigInteger(value).toByte() }.getOrNull()
                    else -> null
                }
            }
            UByte::class -> {
                when (value) {
                    is Number -> value.toByte().toUByte()
                    is String -> runCatching { BigInteger(value).toByte().toUByte() }.getOrNull()
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
                    is String -> runCatching { BigInteger(value).toShort() }.getOrNull()
                    else -> null
                }
            }
            UShort::class -> {
                when (value) {
                    is Number -> value.toShort().toUShort()
                    is String -> runCatching { BigInteger(value).toShort().toUShort() }.getOrNull()
                    else -> null
                }
            }
            UUID::class -> runCatching { UUID.fromString(value.toString()) }.getOrNull()
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = type.arguments[0].type!!
                when (value) {
                    is List<*> -> {
                        value.map { deserialize(projectionMap, type0, it) }
                    }
                    else -> {
                        listOf(deserialize(projectionMap, type0, value))
                    }
                }.run {
                    when (classifier) {
                        Set::class -> toSet()
                        HashSet::class -> toHashSet()
                        LinkedHashSet::class -> LinkedHashSet(this)
                        else -> this
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
                        return@mapNotNull null to deserialize(projectionMap, type1, value)
                    }
                    deserializeKey(type0, key.toString())?.let {
                        it to deserialize(projectionMap, type1, value)
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
                        constructor.callByValues(projectionMap(classifier, type), values)
                    }
                }
            }
            is KTypeParameter -> {
                deserialize(projectionMap, projectionMap[classifier]!!.type!!, value)
            }
            else -> throw UnsupportedTypeException(type, "value")
        }
    }

    private fun deserializeKey(type: KType, key: String): Any? {
        return when (type.classifier) {
            String::class -> key
            Int::class -> runCatching { BigInteger(key).toInt() }.getOrNull()
            UInt::class -> runCatching { BigInteger(key).toInt().toUInt() }.getOrNull()
            Boolean::class -> key.toBooleanStrictOrNull()
            Double::class -> key.toDoubleOrNull()
            Float::class -> key.toFloatOrNull()
            Long::class -> runCatching { BigInteger(key).toLong() }.getOrNull()
            ULong::class -> runCatching { BigInteger(key).toLong().toULong() }.getOrNull()
            Byte::class -> runCatching { BigInteger(key).toByte() }.getOrNull()
            UByte::class -> runCatching { BigInteger(key).toByte().toUByte() }.getOrNull()
            Char::class -> key.singleOrNull()
            Short::class -> runCatching { BigInteger(key).toShort() }.getOrNull()
            UShort::class -> runCatching { BigInteger(key).toShort().toUShort() }.getOrNull()
            UUID::class -> runCatching { UUID.fromString(key) }.getOrNull()
            else -> throw UnsupportedTypeException(type, "key")
        }
    }

    private fun serialize(projectionMap: Map<KTypeParameter, KTypeProjection>, section: ConfigurationSection, type: KType, value: Any?): Any? {
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
                (value as Iterable<*>).map { serialize(projectionMap, section, type0, it) }
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
                val type0 = type.arguments[0].type!!
                val type1 = type.arguments[1].type!!
                (value as Map<*, *>).map {
                    serializeKey(type0, it.key) to serialize(projectionMap, section.createSection(it.key.toString()), type1, it.value)
                }.toMap()
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
                        section.set(classifier, type, value)
                        // Return Unit so ignores the result
                    }
                }
            }
            is KTypeParameter -> {
                serialize(projectionMap, section, projectionMap[classifier]!!.type!!, value)
            }
            else -> value
        }
    }

    private fun serializeKey(type: KType, key: Any?): Any? {
        if (key == null) return null
        return when (type.classifier) {
            String::class -> key
            Int::class -> key
            UInt::class -> (key as UInt).toLong()
            Boolean::class -> key
            Double::class -> key
            Float::class -> key
            Long::class -> key
            ULong::class -> BigInteger(key.toString())
            Byte::class -> key
            UByte::class -> (key as UByte).toShort()
            Char::class -> key
            Short::class -> key
            UShort::class -> (key as UShort).toInt()
            UUID::class -> key.toString()
            else -> throw UnsupportedTypeException(type, "key")
        }
    }
}
