package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.exception.TypeMismatchException
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.jvm.isAccessible

internal sealed class Content<T>(val type: KType) {
    sealed class Keyable<T>(type: KType) : Content<T>(type)

    abstract fun deserialize(deserializer: Deserializer, path: String, value: Any): T?
    abstract fun serialize(path: String, value: T?): Any?

    class StringType(type: KType) : Keyable<String>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = value.toString()
        override fun serialize(path: String, value: String?) = value
    }

    class IntType(type: KType) : Keyable<Int>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.int(value)
        override fun serialize(path: String, value: Int?) = value
    }

    class UIntType(type: KType) : Keyable<UInt>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.uint(value)
        override fun serialize(path: String, value: UInt?) = value?.toLong()
    }

    class BooleanType(type: KType) : Keyable<Boolean>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.boolean(value)
        override fun serialize(path: String, value: Boolean?) = value
    }

    class DoubleType(type: KType) : Keyable<Double>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.double(value)
        override fun serialize(path: String, value: Double?) = value
    }

    class FloatType(type: KType) : Keyable<Float>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.float(value)
        override fun serialize(path: String, value: Float?) = value
    }

    class LongType(type: KType) : Keyable<Long>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.long(value)
        override fun serialize(path: String, value: Long?) = value
    }

    class ULongType(type: KType) : Keyable<ULong>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.ulong(value)
        override fun serialize(path: String, value: ULong?) = value?.toString()?.let(::BigInteger)
    }

    class ByteType(type: KType) : Keyable<Byte>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.byte(value)
        override fun serialize(path: String, value: Byte?) = value
    }

    class UByteType(type: KType) : Keyable<UByte>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.ubyte(value)
        override fun serialize(path: String, value: UByte?) = value?.toShort()
    }

    class CharType(type: KType) : Keyable<Char>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.char(value)
        override fun serialize(path: String, value: Char?) = value
    }

    class ShortType(type: KType) : Keyable<Short>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.short(value)
        override fun serialize(path: String, value: Short?) = value
    }

    class UShortType(type: KType) : Keyable<UShort>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.ushort(value)
        override fun serialize(path: String, value: UShort?) = value?.toInt()
    }

    class BigIntegerType(type: KType) : Keyable<BigInteger>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.bigInteger(value)
        override fun serialize(path: String, value: BigInteger?) = value
    }

    class BigDecimalType(type: KType) : Keyable<BigDecimal>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.bigDecimal(value)
        override fun serialize(path: String, value: BigDecimal?) = value?.toString()
    }

    class DateType(type: KType) : Keyable<Date>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.date(value)
        override fun serialize(path: String, value: Date?) = value
    }

    class CalendarType(type: KType) : Keyable<Calendar>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.calendar(value)
        override fun serialize(path: String, value: Calendar?) = value?.time
    }

    class UUIDType(type: KType) : Keyable<UUID>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.uuid(value)
        override fun serialize(path: String, value: UUID?) = value?.toString()
    }

    sealed class IterableType<T : Iterable<U>, U : Any?>(type: KType, protected val content: Content<U>, protected val isMarkedNullable: Boolean) : Content<T>(type) {
        override fun serialize(path: String, value: T?) = value?.mapIndexed { index, v ->
            content.serialize("$path[$index]", v)
        }

        class ListType<U : Any?>(type: KType, content: Content<U>, isMarkedNullable: Boolean) : IterableType<List<U>, U>(type, content, isMarkedNullable) {
            override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.list(content, path, value, isMarkedNullable)
        }

        class SetType<U : Any?>(type: KType, content: Content<U>, isMarkedNullable: Boolean) : IterableType<Set<U>, U>(type, content, isMarkedNullable) {
            override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.list(content, path, value, isMarkedNullable).toSet()
        }

        class HashSetType<U>(type: KType, content: Content<U>, isMarkedNullable: Boolean) : IterableType<HashSet<U>, U>(type, content, isMarkedNullable) {
            override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.list(content, path, value, isMarkedNullable).toHashSet()
        }

        class LinkedHashSetType<U>(type: KType, content: Content<U>, isMarkedNullable: Boolean) : IterableType<LinkedHashSet<U>, U>(type, content, isMarkedNullable) {
            override fun deserialize(deserializer: Deserializer, path: String, value: Any) = LinkedHashSet(deserializer.list(content, path, value, isMarkedNullable))
        }
    }

    class MapType<K, V>(type: KType, private val keyable: Keyable<K>, private val content: Content<V>, private val isMarkedNullable: Boolean) : Content<Map<K, V?>>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.map(type, keyable, content, path, value, isMarkedNullable)
        override fun serialize(path: String, value: Map<K, V?>?) = value?.map { (k, v) ->
            keyable.serialize(path, k) to v?.let { content.serialize(path, v) }
        }?.toMap()
    }

    class ConfigurationSerializableType(type: KType) : Content<ConfigurationSerializable>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = value as? ConfigurationSerializable
        override fun serialize(path: String, value: ConfigurationSerializable?) = value
    }

    class EnumType(type: KType, private val classifier: KClass<*>) : Keyable<Enum<*>>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = deserializer.enum(classifier, value)
        override fun serialize(path: String, value: Enum<*>?) = value?.name
    }

    class SectionType<T : Any>(type: KType, private val constructor: KFunction<T>, private val properties: Collection<KProperty1<T, *>>, private val contents: Map<KParameter, Content<Any?>>) : Content<T>(type) {
        private val nameMap = contents.entries.associate { it.key.name!! to it.value }

        override fun deserialize(deserializer: Deserializer, path: String, value: Any): T {
            val values = when (value) {
                is ConfigurationSection -> value.getValues(false)
                is Map<*, *> -> value.entries.filterIsInstance<Map.Entry<String, Any?>>().associate { it.key to it.value }
                else -> throw TypeMismatchException(type, value, path)
            }
            return contents.entries.mapNotNull { (parameter, content) ->
                val name = parameter.name!!
                val mapPath = if (path.isEmpty()) name else "$path.$name"
                val mapValue = values[name]
                val deserializeValue = mapValue?.let { content.deserialize(deserializer, mapPath, it) }
                when {
                    deserializeValue != null -> {
                        parameter to deserializeValue
                    }
                    content.type.isMarkedNullable -> {
                        parameter to null
                    }
                    parameter.isOptional -> {
                        null
                    }
                    else -> {
                        throw TypeMismatchException(content.type, mapValue, mapPath)
                    }
                }
            }.toMap().run(constructor::callBy)
        }

        override fun serialize(path: String, value: T?) = Section(
            value?.let {
                properties.associate { property ->
                    property.isAccessible = true
                    property.name to Section.Value(property.findComment(), nameMap[property.name]?.serialize(path, property.get(value)))
                }
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    class UseSerializerType<T, Z>(type: KType, private val serializer: KtConfigSerializer<T, Z>, private val content: Content<Any?>) : Content<Z>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any): Z? = serializer.deserialize(content.deserialize(deserializer, path, value) as T)
        override fun serialize(path: String, value: Z?) = value?.let { content.serialize(path, serializer.serialize(value)) }

        class Keyable<T, Z>(type: KType, serializer: KtConfigSerializer<T, Z>, content: Content<Any?>) : Content.Keyable<Z>(type) {
            private val useSerializerType = UseSerializerType(type, serializer, content)

            override fun deserialize(deserializer: Deserializer, path: String, value: Any) = useSerializerType.deserialize(deserializer, path, value)
            override fun serialize(path: String, value: Z?) = useSerializerType.serialize(path, value)
        }
    }

    class Cache<T>(type: KType, val content: () -> Content<T>) : Content<T>(type) {
        override fun deserialize(deserializer: Deserializer, path: String, value: Any) = content().deserialize(deserializer, path, value)
        override fun serialize(path: String, value: T?) = content().serialize(path, value)
    }
}
