package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.KtConfigSetting
import dev.s7a.ktconfig.exception.TypeMismatchException
import org.bukkit.configuration.ConfigurationSection
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ValueConverter(private val setting: KtConfigSetting) {
    @Suppress("DEPRECATION")
    private val safeConstructor = SafeConstructor()
    private val constructYamlBool = safeConstructor.ConstructYamlBool()
    private val constructYamlInt = safeConstructor.ConstructYamlInt()
    private val constructYamlFloat = safeConstructor.ConstructYamlFloat()
    private val constructYamlTimestamp = SafeConstructor.ConstructYamlTimestamp()

    private fun node(value: String): ScalarNode {
        @Suppress("DEPRECATION")
        return ScalarNode(Tag.STR, value, null, null, null as Char?)
        // return ScalarNode(Tag.STR, value, null, null, DumperOptions.ScalarStyle.PLAIN)
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

    fun int(value: Number): Int {
        return value.toInt()
    }

    fun int(value: String): Int? {
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

    fun uint(value: String): UInt? {
        return number(value)?.let(::uint)
    }

    fun boolean(value: Any): Boolean? {
        return when (value) {
            is Boolean -> value
            is String -> boolean(value)
            else -> null
        }
    }

    fun boolean(value: String): Boolean? {
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

    fun double(value: String): Double? {
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

    fun float(value: String): Float? {
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

    fun long(value: String): Long? {
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

    fun ulong(value: String): ULong? {
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

    fun byte(value: String): Byte? {
        return number(value)?.let(::byte)
    }

    fun ubyte(value: Any): UByte? {
        return when (value) {
            is Number -> ubyte(value)
            is String -> ubyte(value)
            else -> null
        }
    }

    fun ubyte(value: Number): UByte {
        return byte(value).toUByte()
    }

    fun ubyte(value: String): UByte? {
        return number(value)?.let(::ubyte)
    }

    fun char(value: Any): Char? {
        return when (value) {
            is Number -> char(value)
            is String -> char(value)
            else -> null
        }
    }

    private fun char(value: Number): Char {
        return value.toChar()
    }

    fun char(value: String): Char? {
        return value.singleOrNull()
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

    fun short(value: String): Short? {
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

    fun ushort(value: String): UShort? {
        return number(value)?.let(::ushort)
    }

    fun bigInteger(value: Any): BigInteger? {
        return when (value) {
            is Number -> bigInteger(value)
            is String -> bigInteger(value)
            else -> null
        }
    }

    fun bigInteger(value: Number): BigInteger {
        return BigInteger(value.toString())
    }

    fun bigInteger(value: String): BigInteger? {
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

    fun bigDecimal(value: String): BigDecimal? {
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

    fun date(value: String): Date? {
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

    fun uuid(value: String): UUID? {
        return runCatching { UUID.fromString(value) }.getOrNull()
    }

    inline fun list(type0: KType, value: Any, path: String, deserialize: (Int, Any?) -> Any?): List<Any?> {
        return when (value) {
            is List<*> -> {
                when {
                    type0.isMarkedNullable -> {
                        value.mapIndexed(deserialize)
                    }
                    setting.strictListElement -> {
                        value.mapIndexed { index, v ->
                            deserialize(index, v) ?: throw TypeMismatchException(type0, v, "$path[$index]")
                        }
                    }
                    else -> {
                        value.mapIndexedNotNull(deserialize)
                    }
                }
            }
            else -> {
                val single = deserialize(0, value)
                if (single != null || type0.isMarkedNullable) {
                    listOf(single)
                } else {
                    if (setting.strictListElement) {
                        throw TypeMismatchException(type0, value, "$path[0]")
                    } else {
                        listOf()
                    }
                }
            }
        }
    }

    inline fun map(type: KType, type0: KType, value: Any, path: String, deserializeKey: (KType, String, String) -> Any?, deserialize: (String, Any?) -> Any?): Map<Any?, Any?> {
        val entries = when (value) {
            is ConfigurationSection -> value.getValues(false).entries
            is Map<*, *> -> value.entries
            else -> throw TypeMismatchException(type, value, path)
        }
        return entries.mapNotNull { (key, value) ->
            if (key == "null" && type0.isMarkedNullable) {
                null to deserialize("$path.$key", value)
            } else {
                deserializeKey(type0, key.toString(), "$path.$key").let {
                    if (it != null) {
                        it to deserialize("$path.$key", value)
                    } else {
                        when {
                            type0.isMarkedNullable -> null
                            setting.strictMapElement -> throw TypeMismatchException(type0, key, "$path.$key(key)")
                            else -> null
                        }
                    }
                }
            }
        }.toMap()
    }

    fun enum(classifier: KClass<*>, value: Any): Enum<*>? {
        return try {
            @Suppress("UNCHECKED_CAST")
            java.lang.Enum.valueOf(classifier.java as Class<out Enum<*>>, value.toString())
        } catch (ex: IllegalArgumentException) {
            null
        }
    }
}
