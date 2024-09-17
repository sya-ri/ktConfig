package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.UnsupportedTypeException
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

internal class ContentSerializer {
    private val contentCache = mutableMapOf<Pair<KClass<*>, KType>, Content<*>>()

    private fun from(
        type: KType,
        projectionMap: ProjectionMap,
        serializer: KtConfigSerializer<*, *>,
    ): Content<out Any?> {
        val content = from(serializer.type, projectionMap)
        @Suppress("UNCHECKED_CAST")
        return if (content is Content.Keyable) {
            Content.UseSerializerType.Keyable(type, serializer, content as Content<Any?>)
        } else {
            Content.UseSerializerType(type, serializer, content as Content<Any?>)
        }
    }

    private fun from(
        type: KType,
        projectionMap: ProjectionMap,
    ): Content<out Any?> {
        type.findSerializer()?.let { serializer ->
            return from(type, projectionMap, serializer)
        }
        return when (val classifier = type.classifier) {
            String::class -> Content.StringType(type)
            Int::class -> Content.IntType(type)
            UInt::class -> Content.UIntType(type)
            Boolean::class -> Content.BooleanType(type)
            Double::class -> Content.DoubleType(type)
            Float::class -> Content.FloatType(type)
            Long::class -> Content.LongType(type)
            ULong::class -> Content.ULongType(type)
            Byte::class -> Content.ByteType(type)
            UByte::class -> Content.UByteType(type)
            Char::class -> Content.CharType(type)
            Short::class -> Content.ShortType(type)
            UShort::class -> Content.UShortType(type)
            BigInteger::class -> Content.BigIntegerType(type)
            BigDecimal::class -> Content.BigDecimalType(type)
            Date::class -> Content.DateType(type)
            Calendar::class -> Content.CalendarType(type)
            UUID::class -> Content.UUIDType(type)
            Iterable::class, Collection::class, List::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                Content.IterableType.ListType(type, from(type0, projectionMap), type0.isMarkedNullable)
            }
            Set::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                Content.IterableType.SetType(type, from(type0, projectionMap), type0.isMarkedNullable)
            }
            HashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                Content.IterableType.HashSetType(type, from(type0, projectionMap), type0.isMarkedNullable)
            }
            LinkedHashSet::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                Content.IterableType.LinkedHashSetType(type, from(type0, projectionMap), type0.isMarkedNullable)
            }
            Map::class, HashMap::class, LinkedHashMap::class -> {
                val type0 = projectionMap.typeArgument(type, 0)
                if (type0.isMarkedNullable) throw UnsupportedTypeException(type0, "key must be not-null")
                val keyable = from(type0, projectionMap)
                if (keyable !is Content.Keyable) throw UnsupportedTypeException(type0, "cannot be used as a key")
                val type1 = projectionMap.typeArgument(type, 1)
                Content.MapType(type, keyable, from(type1, projectionMap), type1.isMarkedNullable)
            }
            is KClass<*> -> {
                classifier.findSerializer()?.let { serializer ->
                    return from(type, projectionMap, serializer)
                }
                when {
                    classifier.isSubclassOf(ConfigurationSerializable::class) -> Content.ConfigurationSerializableType(type)
                    classifier.isSubclassOf(Enum::class) -> Content.EnumType(type, classifier)
                    else -> {
                        val cacheKey = classifier to type
                        contentCache[cacheKey] ?: Content.Cache(type) { section(classifier, type, ProjectionMap(classifier, type)) }.run {
                            contentCache[cacheKey] = this
                            content()
                        }
                    }
                }
            }
            is KTypeParameter -> from(projectionMap.type(classifier), projectionMap)
            else -> throw UnsupportedTypeException(type, "use another class")
        }
    }

    fun <T : Any> section(
        clazz: KClass<T>,
        type: KType,
        projectionMap: ProjectionMap,
    ): Content.SectionType<T> {
        val constructor = clazz.primaryConstructor ?: throw UnsupportedTypeException(type, "primary constructor must be defined")
        constructor.isAccessible = true
        val contents =
            constructor.parameters.associateWith {
                @Suppress("UNCHECKED_CAST")
                from(projectionMap.type(it.type), projectionMap) as Content<Any?>
            }

        val declaredIndex = constructor.parameters.associate { it.name to it.index }
        val properties = clazz.memberProperties.sortedBy { declaredIndex[it.name] }
        return Content.SectionType(type, constructor, properties, contents)
    }

    companion object {
        private fun KAnnotatedElement.findSerializer(): KtConfigSerializer<*, *>? {
            val serializer = findAnnotation<UseSerializer>()?.with ?: return null
            return serializer.objectInstance ?: serializer.createInstance()
        }
    }
}
