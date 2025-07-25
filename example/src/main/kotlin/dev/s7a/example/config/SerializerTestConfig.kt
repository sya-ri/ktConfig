package dev.s7a.example.config

import dev.s7a.ktconfig.ForKtConfig

@ForKtConfig
@OptIn(ExperimentalUnsignedTypes::class)
data class SerializerTestConfig(
    val string: String,
    val byte: Byte,
    val char: Char,
    val int: Int,
    val long: Long,
    val short: Short,
    val double: Double,
    val float: Float,
    val uByte: UByte,
    val uInt: UInt,
    val uLong: ULong,
    val uShort: UShort,
    val boolean: Boolean,
    val byteArray: ByteArray,
    val charArray: CharArray,
    val intArray: IntArray,
    val longArray: LongArray,
    val shortArray: ShortArray,
    val doubleArray: DoubleArray,
    val floatArray: FloatArray,
    val uByteArray: UByteArray,
    val uIntArray: UIntArray,
    val uLongArray: ULongArray,
    val uShortArray: UShortArray,
    val booleanArray: BooleanArray,
    val list: List<UInt>,
    val list2: List<List<UInt>>,
    val arrayDeque: ArrayDeque<UInt>,
    val set: Set<UInt>,
    val map: Map<ULong, ULong>,
    val map2: Map<UInt, Map<UInt, UInt>>,
    val listMap: List<Map<UInt, UInt>>,
    val mapList: Map<UInt, List<UInt>>,
    val enum: TestEnum,
    val enumList: List<TestEnum>,
    val enumMap: Map<TestEnum, TestEnum>,
    val value: Value,
    val valueList: ValueList,
    val valueMap: ValueMap,
    val nullable: String?,
) {
    enum class TestEnum {
        A,
        B,
        C,
        D,
    }

    @JvmInline
    value class Value(
        val value: String,
    )

    @JvmInline
    value class ValueList(
        val value: List<Value>,
    )

    @JvmInline
    value class UIntValue(
        val value: UInt,
    )

    @JvmInline
    value class ValueMap(
        val value: Map<Value, UIntValue>,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializerTestConfig

        if (string != other.string) return false
        if (byte != other.byte) return false
        if (char != other.char) return false
        if (int != other.int) return false
        if (long != other.long) return false
        if (short != other.short) return false
        if (double != other.double) return false
        if (float != other.float) return false
        if (uByte != other.uByte) return false
        if (uInt != other.uInt) return false
        if (uLong != other.uLong) return false
        if (uShort != other.uShort) return false
        if (boolean != other.boolean) return false
        if (byteArray.contentEquals(other.byteArray).not()) return false
        if (charArray.contentEquals(other.charArray).not()) return false
        if (intArray.contentEquals(other.intArray).not()) return false
        if (longArray.contentEquals(other.longArray).not()) return false
        if (shortArray.contentEquals(other.shortArray).not()) return false
        if (doubleArray.contentEquals(other.doubleArray).not()) return false
        if (floatArray.contentEquals(other.floatArray).not()) return false
        if (uByteArray.contentEquals(other.uByteArray).not()) return false
        if (uIntArray.contentEquals(other.uIntArray).not()) return false
        if (uLongArray.contentEquals(other.uLongArray).not()) return false
        if (uShortArray.contentEquals(other.uShortArray).not()) return false
        if (booleanArray.contentEquals(other.booleanArray).not()) return false
        if (list != other.list) return false
        if (list2 != other.list2) return false
        if (set != other.set) return false
        if (arrayDeque != other.arrayDeque) return false
        if (map != other.map) return false
        if (map2 != other.map2) return false
        if (listMap != other.listMap) return false
        if (mapList != other.mapList) return false
        if (enum != other.enum) return false
        if (enumList != other.enumList) return false
        if (enumMap != other.enumMap) return false
        if (value != other.value) return false
        if (valueList != other.valueList) return false
        if (valueMap != other.valueMap) return false
        if (nullable != other.nullable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = string.hashCode()
        result = 31 * result + byte.toInt()
        result = 31 * result + char.hashCode()
        result = 31 * result + int
        result = 31 * result + long.hashCode()
        result = 31 * result + short
        result = 31 * result + double.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + uByte.hashCode()
        result = 31 * result + uInt.hashCode()
        result = 31 * result + uLong.hashCode()
        result = 31 * result + uShort.hashCode()
        result = 31 * result + boolean.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        result = 31 * result + charArray.contentHashCode()
        result = 31 * result + intArray.contentHashCode()
        result = 31 * result + longArray.contentHashCode()
        result = 31 * result + shortArray.contentHashCode()
        result = 31 * result + doubleArray.contentHashCode()
        result = 31 * result + floatArray.contentHashCode()
        result = 31 * result + uByteArray.contentHashCode()
        result = 31 * result + uIntArray.contentHashCode()
        result = 31 * result + uLongArray.contentHashCode()
        result = 31 * result + uShortArray.contentHashCode()
        result = 31 * result + booleanArray.contentHashCode()
        result = 31 * result + list.hashCode()
        result = 31 * result + list2.hashCode()
        result = 31 * result + set.hashCode()
        result = 31 * result + arrayDeque.hashCode()
        result = 31 * result + map.hashCode()
        result = 31 * result + map2.hashCode()
        result = 31 * result + listMap.hashCode()
        result = 31 * result + mapList.hashCode()
        result = 31 * result + enum.hashCode()
        result = 31 * result + enumList.hashCode()
        result = 31 * result + enumMap.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + valueList.hashCode()
        result = 31 * result + valueMap.hashCode()
        result = 31 * result + (nullable?.hashCode() ?: 0)
        return result
    }
}
