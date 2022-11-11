package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setComment
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setHeaderComment
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
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

internal object KtConfigSerialization {
    /**
     * Change the path separator to be able to use Double or Float as key
     */
    private const val pathSeparator = 0x00.toChar()

    private fun projectionMap(clazz: KClass<*>, type: KType): Map<KTypeParameter, KTypeProjection> {
        return clazz.typeParameters.mapIndexed { index, parameter ->
            parameter to type.arguments[index]
        }.toMap()
    }

    fun <T : Any> deserialize(clazz: KClass<T>, type: KType, text: String): T? {
        val constructor = clazz.primaryConstructor ?: return null
        val values = YamlConfiguration().apply {
            options().pathSeparator(pathSeparator)
            loadFromString(text)
        }.getValues(false)
        return constructor.callByValues(projectionMap(clazz, type), values)
    }

    private fun KAnnotatedElement.findComment(): List<String>? {
        return findAnnotation<Comment>()?.lines?.toList()
    }

    private fun ConfigurationSection.set(clazz: KClass<*>, type: KType, value: Any) {
        clazz.memberProperties.forEach {
            serialize(projectionMap(clazz, type), createSection(it.name), it.returnType, it.call(value)).run {
                if (this !is Unit) {
                    // Unit is that should be ignored
                    set(it.name, this)
                }
            }
            setComment(it.name, it.findComment())
        }
    }

    fun <T : Any> serialize(clazz: KClass<T>, type: KType, value: T): String {
        return YamlConfiguration().apply {
            options().pathSeparator(pathSeparator).setHeaderComment(clazz.findComment())
            set(clazz, type, value)
        }.saveToString()
    }

    private fun <T> KFunction<T>.callByValues(projectionMap: Map<KTypeParameter, KTypeProjection>, values: Map<String, Any?>): T? {
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
        val value = when {
            contains(name) -> deserialize(projectionMap, type, get(name))
            parameter.isOptional -> {} // Use default value: Unit
            else -> null
        }
        if (value == null && type.isMarkedNullable.not()) {
            throw TypeMismatchException(type, null)
        }
        return value
    }

    private fun deserialize(projectionMap: Map<KTypeParameter, KTypeProjection>, type: KType, value: Any?): Any? {
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
            Int::class -> key.toIntOrNull()
            UInt::class -> key.toUIntOrNull()
            Boolean::class -> key.toBooleanStrictOrNull()
            Double::class -> key.toDoubleOrNull()
            Float::class -> key.toFloatOrNull()
            Long::class -> key.toLongOrNull()
            ULong::class -> key.toULongOrNull()
            Byte::class -> key.toByteOrNull()
            UByte::class -> key.toUByteOrNull()
            Char::class -> key.singleOrNull()
            Short::class -> key.toShortOrNull()
            UShort::class -> key.toUShortOrNull()
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
                    serializeKey(type0, it.key.toString()) to serialize(projectionMap, section.createSection(it.key.toString()), type1, it.value)
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

    private fun serializeKey(type: KType, key: String): Any? {
        return when (type.classifier) {
            String::class -> key
            Int::class -> key
            UInt::class -> key.toLong()
            Boolean::class -> key.toBooleanStrictOrNull()
            Double::class -> key.toDoubleOrNull()
            Float::class -> key.toFloatOrNull()
            Long::class -> key
            ULong::class -> key.toLongOrNull()?.takeUnless { it < 0 } ?: key
            Byte::class -> key
            UByte::class -> key.toShort()
            Char::class -> key
            Short::class -> key
            UShort::class -> key.toInt()
            UUID::class -> key
            else -> throw UnsupportedTypeException(type, "key")
        }
    }
}
