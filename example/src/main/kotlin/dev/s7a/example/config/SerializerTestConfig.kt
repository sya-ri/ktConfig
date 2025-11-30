package dev.s7a.example.config

import dev.s7a.example.serializer.FormattedVector
import dev.s7a.example.serializer.FormattedVectorSerializer
import dev.s7a.example.serializer.OverrideIncorrectString
import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.PathName
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.UUID

@KtConfig
@Comment("Header comment", "Second line in header")
@OptIn(ExperimentalUnsignedTypes::class)
data class SerializerTestConfig(
    @Comment("Property comment", "Second line in property")
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
    val uuid: UUID,
    val localTime: LocalTime,
    val localDate: LocalDate,
    val localDateTime: LocalDateTime,
    val year: Year,
    val yearMonth: YearMonth,
    val offsetTime: OffsetTime,
    val offsetDateTime: OffsetDateTime,
    val zonedDateTime: ZonedDateTime,
    val instant: Instant,
    val duration: Duration,
    val period: Period,
    val itemStack: ItemStack,
    val location: Location,
    val formattedVector: FormattedVector,
    val formattedVector2:
        @UseSerializer(FormattedVectorSerializer::class)
        Vector,
    val overrideSerializerString: OverrideIncorrectString,
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
    val array: Array<String>,
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
    val nested: Nested,
    val nestedList: List<Nested>,
    val nestedMap: Map<String, Nested>,
    val nullable: String?,
    val nullableList: List<String?>,
    val nullableArray: Array<String?>,
    val nullableArrayDeque: ArrayDeque<String?>,
    val nullableSet: Set<String?>,
    val nullableMap: Map<String, String?>,
    val nullableMap2: Map<String, Map<String, String?>>,
    val nullableListMap: List<Map<String, String?>>,
    val nullableMapList: Map<String, List<String?>>,
    val nullableListNullableMap: List<Map<String, String?>?>,
    @PathName("path-name")
    val pathName: String,
    val listPathName: List<NestedPathName>,
    val mapPathName: Map<String, NestedPathName>,
) {
    enum class TestEnum {
        A,
        B,
        C,
        D,
        E,
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

    @KtConfig
    data class Nested(
        val string: String,
        val uint: UInt,
        val nested: Nested?,
    )

    @KtConfig
    data class NestedPathName(
        @PathName("path-name")
        val string: String,
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
        if (uuid != other.uuid) return false
        if (localTime != other.localTime) return false
        if (localDate != other.localDate) return false
        if (localDateTime != other.localDateTime) return false
        if (year != other.year) return false
        if (yearMonth != other.yearMonth) return false
        if (offsetTime != other.offsetTime) return false
        if (offsetDateTime != other.offsetDateTime) return false
        if (zonedDateTime != other.zonedDateTime) return false
        if (instant != other.instant) return false
        if (duration != other.duration) return false
        if (period != other.period) return false
        if (itemStack != other.itemStack) return false
        if (location != other.location) return false
        if (formattedVector != other.formattedVector) return false
        if (formattedVector2 != other.formattedVector2) return false
        if (overrideSerializerString != other.overrideSerializerString) return false
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
        if (array.contentEquals(other.array).not()) return false
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
        if (nested != other.nested) return false
        if (nestedList != other.nestedList) return false
        if (nestedMap != other.nestedMap) return false
        if (nullable != other.nullable) return false
        if (nullableList != other.nullableList) return false
        if (nullableArray.contentEquals(other.nullableArray).not()) return false
        if (nullableArrayDeque != other.nullableArrayDeque) return false
        if (nullableSet != other.nullableSet) return false
        if (nullableMap != other.nullableMap) return false
        if (nullableMap2 != other.nullableMap2) return false
        if (nullableListMap != other.nullableListMap) return false
        if (nullableMapList != other.nullableMapList) return false
        if (nullableListNullableMap != other.nullableListNullableMap) return false
        if (pathName != other.pathName) return false
        if (listPathName != other.listPathName) return false
        if (mapPathName != other.mapPathName) return false

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
        result = 31 * result + uuid.hashCode()
        result = 31 * result + localTime.hashCode()
        result = 31 * result + localDate.hashCode()
        result = 31 * result + localDateTime.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + yearMonth.hashCode()
        result = 31 * result + offsetTime.hashCode()
        result = 31 * result + offsetDateTime.hashCode()
        result = 31 * result + zonedDateTime.hashCode()
        result = 31 * result + instant.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + period.hashCode()
        result = 31 * result + itemStack.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + formattedVector.hashCode()
        result = 31 * result + formattedVector2.hashCode()
        result = 31 * result + overrideSerializerString.hashCode()
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
        result = 31 * result + array.contentHashCode()
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
        result = 31 * result + nested.hashCode()
        result = 31 * result + nestedList.hashCode()
        result = 31 * result + nestedMap.hashCode()
        result = 31 * result + (nullable?.hashCode() ?: 0)
        result = 31 * result + nullableList.hashCode()
        result = 31 * result + nullableArray.contentHashCode()
        result = 31 * result + nullableArrayDeque.hashCode()
        result = 31 * result + nullableSet.hashCode()
        result = 31 * result + nullableMap.hashCode()
        result = 31 * result + nullableMap2.hashCode()
        result = 31 * result + nullableListMap.hashCode()
        result = 31 * result + nullableMapList.hashCode()
        result = 31 * result + nullableListNullableMap.hashCode()
        result = 31 * result + pathName.hashCode()
        result = 31 * result + listPathName.hashCode()
        result = 31 * result + mapPathName.hashCode()
        return result
    }
}
