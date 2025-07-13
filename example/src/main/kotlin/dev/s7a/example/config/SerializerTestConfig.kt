package dev.s7a.example.config

import dev.s7a.ktconfig.ForKtConfig

@ForKtConfig
@OptIn(ExperimentalUnsignedTypes::class)
data class SerializerTestConfig(
    val byte: Byte,
    val char: Char,
    val double: Double,
    val float: Float,
    val int: Int,
    val long: Long,
    val short: Short,
    val string: String,
    val uByte: UByte,
    val uInt: UInt,
    val uLong: ULong,
    val uShort: UShort,
    val list: List<String>,
    val set: Set<String>,
    val arrayDeque: ArrayDeque<String>,
    val uByteArray: UByteArray,
    val uIntArray: UIntArray,
    val uLongArray: ULongArray,
    val uShortArray: UShortArray,
    val nullable: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializerTestConfig

        if (byte != other.byte) return false
        if (char != other.char) return false
        if (double != other.double) return false
        if (float != other.float) return false
        if (int != other.int) return false
        if (long != other.long) return false
        if (short != other.short) return false
        if (string != other.string) return false
        if (uByte != other.uByte) return false
        if (uInt != other.uInt) return false
        if (uLong != other.uLong) return false
        if (uShort != other.uShort) return false
        if (list != other.list) return false
        if (set != other.set) return false
        if (arrayDeque != other.arrayDeque) return false
        if (!uByteArray.contentEquals(other.uByteArray)) return false
        if (!uIntArray.contentEquals(other.uIntArray)) return false
        if (!uLongArray.contentEquals(other.uLongArray)) return false
        if (!uShortArray.contentEquals(other.uShortArray)) return false
        if (nullable != other.nullable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = byte.toInt()
        result = 31 * result + char.hashCode()
        result = 31 * result + double.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + int
        result = 31 * result + long.hashCode()
        result = 31 * result + short
        result = 31 * result + string.hashCode()
        result = 31 * result + uByte.hashCode()
        result = 31 * result + uInt.hashCode()
        result = 31 * result + uLong.hashCode()
        result = 31 * result + uShort.hashCode()
        result = 31 * result + list.hashCode()
        result = 31 * result + set.hashCode()
        result = 31 * result + arrayDeque.hashCode()
        result = 31 * result + uByteArray.contentHashCode()
        result = 31 * result + uIntArray.contentHashCode()
        result = 31 * result + uLongArray.contentHashCode()
        result = 31 * result + uShortArray.contentHashCode()
        result = 31 * result + (nullable?.hashCode() ?: 0)
        return result
    }
}
