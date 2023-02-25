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

    private fun KType.findSerializer(): KtConfigSerializer? {
        val serializer = findAnnotation<UseSerializer>()?.with ?: return null
        return serializer.objectInstance ?: serializer.createInstance()
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
        val projectionMap = projectionMap(clazz, type)
        clazz.memberProperties.forEach {
            if (it.javaField == null) {
                // Ignore custom getters
                // https://discuss.kotlinlang.org/t/reflection-and-properties-checking-for-custom-getters-setters/22457/2
                return@forEach
            }
            it.isAccessible = true
            serialize(projectionMap, createSection(it.name), it.returnType, it.call(value)).run {
                if (this !is Unit) {
                    // Unit is that should be ignored
                    set(it.name, this)
                }
            }
            setComment(it.name, it.findComment())
        }
    }

    private fun deserialize(projectionMap: Map<KTypeParameter, KTypeProjection>, type: KType, value: Any?): Any? {
        if (value == null) return null
        type.findSerializer()?.let {
            return it.deserialize(deserialize(projectionMap, it.type, value))
        }
        return when (val classifier = type.classifier) {
            String::class -> value.toString()
            Int::class -> ValueConverter.int(value)
            UInt::class -> ValueConverter.uint(value)
            Boolean::class -> ValueConverter.boolean(value)
            Double::class -> ValueConverter.double(value)
            Float::class -> ValueConverter.float(value)
            Long::class -> ValueConverter.long(value)
            ULong::class -> ValueConverter.ulong(value)
            Byte::class -> ValueConverter.byte(value)
            UByte::class -> ValueConverter.ubyte(value)
            Char::class -> ValueConverter.char(value)
            Short::class -> ValueConverter.short(value)
            UShort::class -> ValueConverter.ushort(value)
            BigInteger::class -> ValueConverter.bigInteger(value)
            BigDecimal::class -> ValueConverter.bigDecimal(value)
            Date::class -> ValueConverter.date(value)
            UUID::class -> ValueConverter.uuid(value)
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                ValueConverter.list(type0, value) {
                    deserialize(projectionMap, type0, it)
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
                val type0 = projectionMap.typeArgument(type, 0)
                val type1 = projectionMap.typeArgument(type, 1)
                ValueConverter.map(type, type0, value, ::deserializeKey) {
                    deserialize(projectionMap, type1, it)
                }
            }
            is KClass<*> -> {
                when {
                    classifier.isSubclassOf(ConfigurationSerializable::class) -> value
                    classifier.isSubclassOf(Enum::class) -> ValueConverter.enum(classifier, value)
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
        type.findSerializer()?.let {
            return it.deserialize(deserializeKey(it.type, key))
        }
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
            return serialize(projectionMap, section, it.type, it.serialize(value))
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
        type.findSerializer()?.let {
            return serializeKey(it.type, it.serialize(key))
        }
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
}
