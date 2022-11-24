package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setComment
import dev.s7a.ktconfig.internal.YamlConfigurationOptionsReflection.setHeaderComment
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.UUID
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createInstance
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
        val constructor = clazz.primaryConstructor ?: throw UnsupportedTypeException(type, "value")
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

    private fun Map<KTypeParameter, KTypeProjection>.type(type: KType): KType {
        return get(type.classifier)?.type ?: type
    }

    private fun Map<KTypeParameter, KTypeProjection>.typeArgument(type: KType, index: Int): KType {
        return type(type.arguments[index].type!!)
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
        val type = projectionMap.type(parameter.type)
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

    private fun ConfigurationSection.set(clazz: KClass<*>, type: KType, value: Any) {
        projectionMap(clazz, type).run {
            clazz.memberProperties.forEach {
                if (it.javaField == null) {
                    // Ignore custom getters
                    // https://discuss.kotlinlang.org/t/reflection-and-properties-checking-for-custom-getters-setters/22457/2
                    return@forEach
                }
                it.isAccessible = true
                serialize(this, createSection(it.name), it.returnType, it.call(value)).run {
                    if (this !is Unit) {
                        // Unit is that should be ignored
                        set(it.name, this)
                    }
                }
                setComment(it.name, it.findComment())
            }
        }
    }

    private fun deserialize(projectionMap: Map<KTypeParameter, KTypeProjection>, type: KType, value: Any?): Any? {
        type.findSerializer()?.let {
            return it.deserialize(deserialize(projectionMap, it.type, value))
        }
        return when (val classifier = type.classifier) {
            String::class -> value.toString()
            Int::class -> {
                when (value) {
                    is Number -> ValueConverter.int(value)
                    is String -> ValueConverter.int(value)
                    else -> null
                }
            }
            UInt::class -> {
                when (value) {
                    is Number -> ValueConverter.uint(value)
                    is String -> ValueConverter.uint(value)
                    else -> null
                }
            }
            Boolean::class -> {
                when (value) {
                    is Boolean -> value
                    is String -> ValueConverter.boolean(value)
                    else -> null
                }
            }
            Double::class -> {
                when (value) {
                    is Number -> ValueConverter.double(value)
                    is String -> ValueConverter.double(value)
                    else -> null
                }
            }
            Float::class -> {
                when (value) {
                    is Number -> ValueConverter.float(value)
                    is String -> ValueConverter.float(value)
                    else -> null
                }
            }
            Long::class -> {
                when (value) {
                    is Number -> ValueConverter.long(value)
                    is String -> ValueConverter.long(value)
                    else -> null
                }
            }
            ULong::class -> {
                when (value) {
                    is Number -> ValueConverter.ulong(value)
                    is String -> ValueConverter.ulong(value)
                    else -> null
                }
            }
            Byte::class -> {
                when (value) {
                    is Number -> ValueConverter.byte(value)
                    is String -> ValueConverter.byte(value)
                    else -> null
                }
            }
            UByte::class -> {
                when (value) {
                    is Number -> ValueConverter.ubyte(value)
                    is String -> ValueConverter.ubyte(value)
                    else -> null
                }
            }
            Char::class -> {
                when (value) {
                    is Number -> ValueConverter.char(value)
                    is String -> ValueConverter.char(value)
                    else -> null
                }
            }
            Short::class -> {
                when (value) {
                    is Number -> ValueConverter.short(value)
                    is String -> ValueConverter.short(value)
                    else -> null
                }
            }
            UShort::class -> {
                when (value) {
                    is Number -> ValueConverter.ushort(value)
                    is String -> ValueConverter.ushort(value)
                    else -> null
                }
            }
            BigInteger::class -> {
                when (value) {
                    is Number -> ValueConverter.bigInteger(value)
                    is String -> ValueConverter.bigInteger(value)
                    else -> null
                }
            }
            BigDecimal::class -> {
                when (value) {
                    is Number -> ValueConverter.bigDecimal(value)
                    is String -> ValueConverter.bigDecimal(value)
                    else -> null
                }
            }
            Date::class -> {
                when (value) {
                    is Date -> value
                    is String -> ValueConverter.date(value)
                    else -> null
                }
            }
            UUID::class -> {
                when (value) {
                    is String -> ValueConverter.uuid(value)
                    else -> null
                }
            }
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                when (value) {
                    is List<*> -> {
                        if (type0.isMarkedNullable) {
                            value.map { deserialize(projectionMap, type0, it) }
                        } else {
                            value.mapNotNull { deserialize(projectionMap, type0, it) }
                        }
                    }
                    else -> {
                        val single = deserialize(projectionMap, type0, value)
                        if (single != null || type0.isMarkedNullable) {
                            listOf(single)
                        } else {
                            listOf()
                        }
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
                val type0 = projectionMap.typeArgument(type, 0)
                val type1 = projectionMap.typeArgument(type, 1)
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
            Int::class -> ValueConverter.int(key)
            UInt::class -> ValueConverter.uint(key)
            Boolean::class -> ValueConverter.boolean(key)
            Double::class -> ValueConverter.double(key)
            Float::class -> ValueConverter.float(key)
            Long::class -> ValueConverter.long(key)
            ULong::class -> ValueConverter.ulong(key)
            Byte::class -> ValueConverter.byte(key)
            UByte::class -> ValueConverter.ubyte(key)
            Char::class -> ValueConverter.char(key)
            Short::class -> ValueConverter.short(key)
            UShort::class -> ValueConverter.ushort(key)
            BigInteger::class -> ValueConverter.bigInteger(key)
            BigDecimal::class -> ValueConverter.bigDecimal(key)
            Date::class -> ValueConverter.date(key)
            UUID::class -> ValueConverter.uuid(key)
            else -> throw UnsupportedTypeException(type, "key")
        }
    }

    private fun serialize(projectionMap: Map<KTypeParameter, KTypeProjection>, section: ConfigurationSection, type: KType, value: Any?): Any? {
        if (value == null) return null
        type.findSerializer()?.let {
            return it.serialize(serialize(projectionMap, section, it.type, value))
        }
        return when (val classifier = type.classifier) {
            String::class -> value
            Int::class -> value
            UInt::class -> (value as UInt).toLong()
            Boolean::class -> value
            Double::class -> value
            Float::class -> value
            Long::class -> value
            ULong::class -> BigInteger(value.toString())
            Byte::class -> value
            UByte::class -> (value as UByte).toShort()
            Char::class -> value
            Short::class -> value
            UShort::class -> (value as UShort).toInt()
            BigInteger::class -> value
            BigDecimal::class -> value.toString()
            Date::class -> value
            UUID::class -> value.toString()
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                (value as Iterable<*>).map { serialize(projectionMap, section, type0, it) }
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                val type1 = projectionMap.typeArgument(type, 1)
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
            BigInteger::class -> BigInteger(key.toString())
            BigDecimal::class -> BigDecimal(key.toString()).toString()
            Date::class -> key
            UUID::class -> key.toString()
            else -> throw UnsupportedTypeException(type, "key")
        }
    }

    private fun KType.findSerializer(): KtConfigSerializer? {
        val serializer = findAnnotation<UseSerializer>()?.with ?: return null
        println(serializer)
        return serializer.objectInstance ?: serializer.createInstance()
    }
}
