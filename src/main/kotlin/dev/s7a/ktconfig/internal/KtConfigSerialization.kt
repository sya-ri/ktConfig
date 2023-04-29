package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.KtConfigSetting
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
import java.util.Calendar
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

internal class KtConfigSerialization(private val setting: KtConfigSetting) {
    companion object {
        /**
         * Change the path separator to be able to use Double or Float as key
         */
        private const val pathSeparator = 0x00.toChar()
    }

    private val valueConverter = ValueConverter(setting)

    fun <T : Any> fromString(clazz: KClass<T>, type: KType, text: String): T? {
        val constructor = clazz.primaryConstructor ?: throw UnsupportedTypeException(type, "value", "")
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
            contains(name) -> deserialize(projectionMap, type, get(name), name)
            parameter.isOptional -> {} // Use default value: Unit
            else -> null
        }
        if (value == null && type.isMarkedNullable.not()) {
            throw TypeMismatchException(type, null, name)
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
            serialize(projectionMap, createSection(it.name), it.returnType, it.call(value), it.name).run {
                if (this !is Unit) {
                    // Unit is that should be ignored
                    set(it.name, this)
                }
            }
            setComment(it.name, it.findComment())
        }
    }

    private fun deserialize(projectionMap: Map<KTypeParameter, KTypeProjection>, type: KType, value: Any?, path: String): Any? {
        if (value == null) return null
        type.findSerializer()?.let {
            return it.deserialize(deserialize(projectionMap, it.type, value, path))
        }
        return when (val classifier = type.classifier) {
            String::class -> value.toString()
            Int::class -> valueConverter.int(value)
            UInt::class -> valueConverter.uint(value)
            Boolean::class -> valueConverter.boolean(value)
            Double::class -> valueConverter.double(value)
            Float::class -> valueConverter.float(value)
            Long::class -> valueConverter.long(value)
            ULong::class -> valueConverter.ulong(value)
            Byte::class -> valueConverter.byte(value)
            UByte::class -> valueConverter.ubyte(value)
            Char::class -> valueConverter.char(value)
            Short::class -> valueConverter.short(value)
            UShort::class -> valueConverter.ushort(value)
            BigInteger::class -> valueConverter.bigInteger(value)
            BigDecimal::class -> valueConverter.bigDecimal(value)
            Date::class -> valueConverter.date(value)
            Calendar::class -> valueConverter.calendar(value)
            UUID::class -> valueConverter.uuid(value)
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                valueConverter.list(type0, value, path) { index, v ->
                    deserialize(projectionMap, type0, v, "$path[$index]")
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
                valueConverter.map(type, type0, value, path, ::deserializeKey) { p, v ->
                    deserialize(projectionMap, type1, v, p).let {
                        when {
                            type1.isMarkedNullable -> it
                            setting.strictMapElement -> throw TypeMismatchException(type1, v, p)
                            else -> it
                        }
                    }
                }
            }
            is KClass<*> -> {
                when {
                    classifier.isSubclassOf(ConfigurationSerializable::class) -> value
                    classifier.isSubclassOf(Enum::class) -> valueConverter.enum(classifier, value)
                    else -> {
                        val constructor = classifier.primaryConstructor ?: throw UnsupportedTypeException(type, "value", path)
                        val values = when (value) {
                            is ConfigurationSection -> value.getValues(false)
                            is Map<*, *> -> value.entries.filterIsInstance<Map.Entry<String, Any?>>().associate { it.key to it.value }
                            else -> throw TypeMismatchException(type, value, path)
                        }
                        constructor.callByValues(projectionMap(classifier, type), values)
                    }
                }
            }
            is KTypeParameter -> {
                deserialize(projectionMap, projectionMap[classifier]!!.type!!, value, path)
            }
            else -> throw UnsupportedTypeException(type, "value", path)
        }
    }

    private fun deserializeKey(type: KType, key: String, path: String): Any? {
        type.findSerializer()?.let {
            return it.deserialize(deserializeKey(it.type, key, path))
        }
        return when (type.classifier) {
            String::class -> key
            Int::class -> valueConverter.int(key)
            UInt::class -> valueConverter.uint(key)
            Boolean::class -> valueConverter.boolean(key)
            Double::class -> valueConverter.double(key)
            Float::class -> valueConverter.float(key)
            Long::class -> valueConverter.long(key)
            ULong::class -> valueConverter.ulong(key)
            Byte::class -> valueConverter.byte(key)
            UByte::class -> valueConverter.ubyte(key)
            Char::class -> valueConverter.char(key)
            Short::class -> valueConverter.short(key)
            UShort::class -> valueConverter.ushort(key)
            BigInteger::class -> valueConverter.bigInteger(key)
            BigDecimal::class -> valueConverter.bigDecimal(key)
            Date::class -> valueConverter.date(key)
            Calendar::class -> valueConverter.calendar(key)
            UUID::class -> valueConverter.uuid(key)
            else -> throw UnsupportedTypeException(type, "key", path)
        }
    }

    private fun serialize(projectionMap: Map<KTypeParameter, KTypeProjection>, section: ConfigurationSection, type: KType, value: Any?, path: String): Any? {
        if (value == null) return null
        type.findSerializer()?.let {
            return serialize(projectionMap, section, it.type, it.serialize(value), path)
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
            Calendar::class -> (value as Calendar).time
            UUID::class -> value.toString()
            Iterable::class, Collection::class, List::class, Set::class, HashSet::class, LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                (value as Iterable<*>).mapIndexed { index, v ->
                    serialize(projectionMap, section, type0, v, "$path[$index]")
                }
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                val type1 = projectionMap.typeArgument(type, 1)
                (value as Map<*, *>).map {
                    serializeKey(type0, it.key, "$path.${it.key}") to serialize(projectionMap, section.createSection(it.key.toString()), type1, it.value, "$path.${it.key}")
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
                serialize(projectionMap, section, projectionMap[classifier]!!.type!!, value, path)
            }
            else -> value
        }
    }

    private fun serializeKey(type: KType, key: Any?, path: String): Any? {
        if (key == null) return null
        type.findSerializer()?.let {
            return serializeKey(it.type, it.serialize(key), path)
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
            Calendar::class -> (key as Calendar).time
            UUID::class -> key.toString()
            else -> throw UnsupportedTypeException(type, "key", path)
        }
    }
}
