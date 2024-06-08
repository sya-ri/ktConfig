package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.internal.reflection.SnakeYamlReflection
import org.bukkit.configuration.ConfigurationSection
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.ScalarNode
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class Deserializer {
    private val safeConstructor = SnakeYamlReflection.getSafeConstructor()
    private val constructYamlBool = safeConstructor.ConstructYamlBool()
    private val constructYamlInt = safeConstructor.ConstructYamlInt()
    private val constructYamlFloat = safeConstructor.ConstructYamlFloat()
    private val constructYamlTimestamp = SafeConstructor.ConstructYamlTimestamp()

    private fun node(value: String): ScalarNode {
        return SnakeYamlReflection.getScalarNode(value)
    }

    fun string(value: Any): String {
        return when (value) {
            is ByteArray -> value.decodeToString()
            else -> value.toString()
        }
    }

    private fun number(value: String): Number? {
        return runCatching {
            constructYamlInt.construct(node(value)) as Number
        }.getOrNull()
    }

    fun int(value: Any): Int? {
        return when (value) {
            is Number -> int(value)
            is String -> int(value)
            else -> null
        }
    }

    private fun int(value: Number): Int {
        return value.toInt()
    }

    private fun int(value: String): Int? {
        return number(value)?.let(::int)
    }

    fun uint(value: Any): UInt? {
        return when (value) {
            is Number -> uint(value)
            is String -> uint(value)
            else -> null
        }
    }

    private fun uint(value: Number): UInt {
        return int(value).toUInt()
    }

    private fun uint(value: String): UInt? {
        return number(value)?.let(::uint)
    }

    fun boolean(value: Any): Boolean? {
        return when (value) {
            is Boolean -> value
            is String -> boolean(value)
            else -> null
        }
    }

    private fun boolean(value: String): Boolean? {
        return runCatching {
            constructYamlBool.construct(node(value)) as Boolean
        }.getOrNull()
    }

    fun double(value: Any): Double? {
        return when (value) {
            is Number -> double(value)
            is String -> double(value)
            else -> null
        }
    }

    private fun double(value: Number): Double {
        return value.toDouble()
    }

    private fun double(value: String): Double? {
        return runCatching {
            constructYamlFloat.construct(node(value)) as Double
        }.getOrNull()
    }

    fun float(value: Any): Float? {
        return when (value) {
            is Number -> float(value)
            is String -> float(value)
            else -> null
        }
    }

    private fun float(value: Number): Float {
        return double(value).toFloat()
    }

    private fun float(value: String): Float? {
        return double(value)?.toFloat()
    }

    fun long(value: Any): Long? {
        return when (value) {
            is Number -> long(value)
            is String -> long(value)
            else -> null
        }
    }

    private fun long(value: Number): Long {
        return value.toLong()
    }

    private fun long(value: String): Long? {
        return number(value)?.let(::long)
    }

    fun ulong(value: Any): ULong? {
        return when (value) {
            is Number -> ulong(value)
            is String -> ulong(value)
            else -> null
        }
    }

    private fun ulong(value: Number): ULong {
        return long(value).toULong()
    }

    private fun ulong(value: String): ULong? {
        return number(value)?.let(::ulong)
    }

    fun byte(value: Any): Byte? {
        return when (value) {
            is Number -> byte(value)
            is String -> byte(value)
            else -> null
        }
    }

    private fun byte(value: Number): Byte {
        return value.toByte()
    }

    private fun byte(value: String): Byte? {
        return number(value)?.let(::byte)
    }

    fun ubyte(value: Any): UByte? {
        return when (value) {
            is Number -> ubyte(value)
            is String -> ubyte(value)
            else -> null
        }
    }

    private fun ubyte(value: Number): UByte {
        return byte(value).toUByte()
    }

    private fun ubyte(value: String): UByte? {
        return number(value)?.let(::ubyte)
    }

    fun char(value: Any): Char? {
        return when (value) {
            is Number -> char(value)
            is String -> char(value)
            is ByteArray -> char(value)
            else -> null
        }
    }

    private fun char(value: Number): Char {
        return value.toInt().toChar()
    }

    private fun char(value: String): Char? {
        return value.singleOrNull()
    }

    private fun char(value: ByteArray): Char? {
        return char(value.decodeToString())
    }

    fun short(value: Any): Short? {
        return when (value) {
            is Number -> short(value)
            is String -> short(value)
            else -> null
        }
    }

    private fun short(value: Number): Short {
        return value.toShort()
    }

    private fun short(value: String): Short? {
        return number(value)?.let(::short)
    }

    fun ushort(value: Any): UShort? {
        return when (value) {
            is Number -> ushort(value)
            is String -> ushort(value)
            else -> null
        }
    }

    private fun ushort(value: Number): UShort {
        return short(value).toUShort()
    }

    private fun ushort(value: String): UShort? {
        return number(value)?.let(::ushort)
    }

    fun bigInteger(value: Any): BigInteger? {
        return when (value) {
            is Number -> bigInteger(value)
            is String -> bigInteger(value)
            else -> null
        }
    }

    private fun bigInteger(value: Number): BigInteger {
        return BigInteger(value.toString())
    }

    private fun bigInteger(value: String): BigInteger? {
        return number(value)?.run {
            if (this is BigInteger) {
                this
            } else {
                bigInteger(this)
            }
        }
    }

    fun bigDecimal(value: Any): BigDecimal? {
        return when (value) {
            is Number -> bigDecimal(value)
            is String -> bigDecimal(value)
            else -> null
        }
    }

    private fun bigDecimal(value: Number): BigDecimal {
        return BigDecimal(value.toString())
    }

    private fun bigDecimal(value: String): BigDecimal? {
        return runCatching {
            BigDecimal(value)
        }.getOrNull()
    }

    fun date(value: Any): Date? {
        return when (value) {
            is Date -> value
            is String -> date(value)
            else -> null
        }
    }

    private fun date(value: String): Date? {
        return runCatching {
            constructYamlTimestamp.construct(node(value)) as Date
        }.getOrNull()
    }

    fun calendar(value: Any): Calendar? {
        return when (value) {
            is Date -> calendar(value)
            is String -> calendar(value)
            else -> null
        }
    }

    private fun calendar(value: Date): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            this.time = value
        }
    }

    private fun calendar(value: String): Calendar? {
        return date(value)?.let(::calendar)
    }

    fun uuid(value: Any): UUID? {
        return when (value) {
            is String -> uuid(value)
            else -> null
        }
    }

    private fun uuid(value: String): UUID? {
        return runCatching { UUID.fromString(value) }.getOrNull()
    }

    fun <T : Any?> list(
        content: Content<T>,
        path: String,
        value: Any,
        isMarkedNullable: Boolean,
    ): List<T> {
        val values =
            when (value) {
                is List<*> -> value
                else -> listOf(value)
            }
        return when {
            isMarkedNullable -> {
                values.mapIndexed { index, v ->
                    @Suppress("UNCHECKED_CAST")
                    v?.let { content.deserialize(this, "$path[$index]", v) } as T
                }
            }
            else -> {
                values.mapIndexed { index, v ->
                    v?.let {
                        content.deserialize(this, "$path[$index]", v)
                    } ?: throw TypeMismatchException(content.type, v, "$path[$index]")
                }
            }
        }
    }

    fun <K, V> map(
        type: KType,
        keyable: Content.Keyable<K>,
        content: Content<V>,
        path: String,
        value: Any,
        isMarkedNullable: Boolean,
    ): Map<K, V?> {
        val entries =
            when (value) {
                is ConfigurationSection -> value.getValues(false).entries
                is Map<*, *> -> value.entries
                else -> throw TypeMismatchException(type, value, path)
            }
        return entries.associate { (key, value) ->
            val deserializedKey =
                keyable.deserialize(this, "$path.$key", key.toString()) ?: throw TypeMismatchException(keyable.type, key, "$path.$key(key)")
            val deserializedValue = content.deserialize(this, "$path.$key", value)
            if (deserializedValue == null && isMarkedNullable.not()) {
                throw TypeMismatchException(content.type, value, "$path.$key")
            }
            deserializedKey to deserializedValue
        }
    }

    fun enum(
        classifier: KClass<*>,
        value: Any,
    ): Enum<*>? {
        return try {
            @Suppress("UNCHECKED_CAST")
            java.lang.Enum.valueOf(classifier.java as Class<out Enum<*>>, value.toString())
        } catch (ex: IllegalArgumentException) {
            null
        }
    }
}
