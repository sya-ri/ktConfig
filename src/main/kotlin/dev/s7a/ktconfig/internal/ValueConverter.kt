package dev.s7a.ktconfig.internal

import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.UUID

internal object ValueConverter {
    @Suppress("DEPRECATION")
    private val safeConstructor = SafeConstructor()
    private val constructYamlBool = safeConstructor.ConstructYamlBool()
    private val constructYamlInt = safeConstructor.ConstructYamlInt()
    private val constructYamlFloat = safeConstructor.ConstructYamlFloat()
    private val constructYamlTimestamp = SafeConstructor.ConstructYamlTimestamp()

    private fun node(value: String): ScalarNode {
        // return ScalarNode(Tag.STR, value, null, null, DumperOptions.ScalarStyle.PLAIN)
        @Suppress("DEPRECATION")
        return ScalarNode(Tag.STR, value, null, null, null as Char?)
    }

    private fun number(value: String): Number? {
        return runCatching {
            constructYamlInt.construct(node(value)) as Number
        }.getOrNull()
    }

    fun int(value: Number): Int {
        return value.toInt()
    }

    fun int(value: String): Int? {
        return number(value)?.let(::int)
    }

    fun uint(value: Number): UInt {
        return int(value).toUInt()
    }

    fun uint(value: String): UInt? {
        return number(value)?.let(::uint)
    }

    fun boolean(value: String): Boolean? {
        return runCatching {
            constructYamlBool.construct(node(value)) as Boolean
        }.getOrNull()
    }

    fun double(value: Number): Double {
        return value.toDouble()
    }

    fun double(value: String): Double? {
        return runCatching {
            constructYamlFloat.construct(node(value)) as Double
        }.getOrNull()
    }

    fun float(value: Number): Float {
        return double(value).toFloat()
    }

    fun float(value: String): Float? {
        return double(value)?.toFloat()
    }

    fun long(value: Number): Long {
        return value.toLong()
    }

    fun long(value: String): Long? {
        return number(value)?.let(::long)
    }

    fun ulong(value: Number): ULong {
        return long(value).toULong()
    }

    fun ulong(value: String): ULong? {
        return number(value)?.let(::ulong)
    }

    fun byte(value: Number): Byte {
        return value.toByte()
    }

    fun byte(value: String): Byte? {
        return number(value)?.let(::byte)
    }

    fun ubyte(value: Number): UByte {
        return byte(value).toUByte()
    }

    fun ubyte(value: String): UByte? {
        return number(value)?.let(::ubyte)
    }

    fun char(value: Number): Char {
        return value.toChar()
    }

    fun char(value: String): Char? {
        return value.singleOrNull()
    }

    fun short(value: Number): Short {
        return value.toShort()
    }

    fun short(value: String): Short? {
        return number(value)?.let(::short)
    }

    fun ushort(value: Number): UShort {
        return short(value).toUShort()
    }

    fun ushort(value: String): UShort? {
        return number(value)?.let(::ushort)
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

    fun bigDecimal(value: Number): BigDecimal {
        return BigDecimal(value.toString())
    }

    fun bigDecimal(value: String): BigDecimal? {
        return runCatching {
            BigDecimal(value)
        }.getOrNull()
    }

    fun date(value: String): Date? {
        return runCatching {
            constructYamlTimestamp.construct(node(value)) as Date
        }.getOrNull()
    }

    fun uuid(value: String): UUID? {
        return runCatching { UUID.fromString(value) }.getOrNull()
    }
}
