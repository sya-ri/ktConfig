import dev.s7a.ktconfig.ktConfigString
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class MapDeserializeTest {
    @Test
    fun string_string_map() {
        data class Data(val data: Map<String, String>)

        assertEquals(
            Data(mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f")),
            ktConfigString(
                """
                    data:
                      a: b
                      c: d
                      null: e
                      5: f
                """.trimIndent()
            )
        )
    }

    @Test
    fun nullable_string_string_map() {
        data class Data(val data: Map<String?, String>)

        assertEquals(
            Data(mapOf("a" to "b", "c" to "d", null to "e")),
            ktConfigString(
                """
                    data:
                      a: b
                      c: d
                      null: e
                """.trimIndent()
            )
        )
    }

    @Test
    fun int_string_map() {
        data class Data(val data: Map<Int, String>)

        assertEquals(
            Data(mapOf(0 to "ab", -1 to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun nullable_int_string_map() {
        data class Data(val data: Map<Int?, String>)

        assertEquals(
            Data(mapOf(0 to "ab", -1 to "c", null to "e")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                      DDD: d
                      null: e
                      FFF: f
                """.trimIndent()
            )
        )
    }

    @Test
    fun uint_string_map() {
        data class Data(val data: Map<UInt, String>)

        assertEquals(
            Data(mapOf(0U to "ab", UInt.MAX_VALUE to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UInt.MAX_VALUE}: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun boolean_string_map() {
        data class Data(val data: Map<Boolean, String>)

        assertEquals(
            Data(mapOf(true to "ab", false to "c")),
            ktConfigString(
                """
                    data:
                      true: ab
                      c: ignore
                      false: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun float_string_map() {
        data class Data(val data: Map<Float, String>)

        assertEquals(
            Data(mapOf(0.5F to "ab", 1.0F to "c", Float.MAX_VALUE to "d", Float.MIN_VALUE to "e")),
            ktConfigString(
                """
                    data:
                      0.5: ab
                      1.0: c
                      3.4028235E38: d
                      1.4E-45: e
                    
                """.trimIndent()
            )
        )
    }

    @Test
    fun double_string_map() {
        data class Data(val data: Map<Double, String>)

        assertEquals(
            Data(mapOf(0.5 to "ab", 1.0 to "c", Double.MAX_VALUE to "d", Double.MIN_VALUE to "e")),
            ktConfigString(
                """
                    data:
                      0.5: ab
                      1.0: c
                      1.7976931348623157E308: d
                      4.9E-324: e
                    
                """.trimIndent()
            )
        )
    }

    @Test
    fun long_string_map() {
        data class Data(val data: Map<Long, String>)

        assertEquals(
            Data(mapOf(0L to "ab", -1L to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun ulong_string_map() {
        data class Data(val data: Map<ULong, String>)

        assertEquals(
            Data(mapOf(0.toULong() to "ab", ULong.MAX_VALUE to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      ${ULong.MAX_VALUE}: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun byte_string_map() {
        data class Data(val data: Map<Byte, String>)

        assertEquals(
            Data(mapOf(0.toByte() to "ab", (-1).toByte() to "c", 0x5E.toByte() to "d")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                      0x5E: d
                """.trimIndent()
            )
        )
    }

    @Test
    fun ubyte_string_map() {
        data class Data(val data: Map<UByte, String>)

        assertEquals(
            Data(mapOf(0.toUByte() to "ab", UByte.MAX_VALUE to "c", 0x5E.toUByte() to "d")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UByte.MAX_VALUE}: c
                      0x5E: d
                """.trimIndent()
            )
        )
    }

    @Test
    fun char_string_map() {
        data class Data(val data: Map<Char, String>)

        assertEquals(
            Data(mapOf('0' to "ab", 'd' to "e")),
            ktConfigString(
                """
                    data:
                      0: ab
                      cc: ignore
                      d: e
                """.trimIndent()
            )
        )
    }

    @Test
    fun short_string_map() {
        data class Data(val data: Map<Short, String>)

        assertEquals(
            Data(mapOf(0.toShort() to "ab", (-1).toShort() to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun ushort_string_map() {
        data class Data(val data: Map<UShort, String>)

        assertEquals(
            Data(mapOf(0.toUShort() to "ab", UShort.MAX_VALUE to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UShort.MAX_VALUE}: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun big_integer_string_map() {
        data class Data(val data: Map<BigInteger, String>)

        assertEquals(
            Data(mapOf(BigInteger.ZERO to "ab", BigInteger("1000000000000000000000000000") to "c")),
            ktConfigString(
                """
                    data:
                      0: ab
                      c: ignore
                      1000000000000000000000000000: c
                """.trimIndent()
            )
        )
    }

    @Test
    fun big_decimal_string_map() {
        data class Data(val data: Map<BigDecimal, String>)

        assertEquals(
            Data(mapOf(BigDecimal.ZERO to "ab", BigDecimal("1000000000000000000000000000") to "c", BigDecimal("1.0E-100") to "d", BigDecimal("1.0E-1000") to "e")),
            ktConfigString(
                """
                    data:
                      '0': ab
                      'c': ignore
                      '1000000000000000000000000000': c
                      '1.0E-100': d
                      '1.0E-1000': e
                """.trimIndent()
            )
        )
    }

    @Test
    fun date_string_map() {
        data class Data(val data: Map<Date, String>)

        assertEquals(
            Data(mapOf(Date(946771200000) to "ab", Date(946856085000) to "c", Date(946856085678) to "d")),
            ktConfigString(
                """
                    data:
                      '2000-01-02T00:00:00Z': ab
                      '2000-01-02T23:34:45Z': c
                      '2000-01-02T23:34:45.678Z': d
                """.trimIndent()
            )
        )
    }

    @Test
    fun calendar_string_map() {
        data class Data(val data: Map<Calendar, String>)

        fun calendar(date: Long): Calendar {
            return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { this.time = Date(date) }
        }

        assertEquals(
            Data(mapOf(calendar(946771200000) to "ab", calendar(946856085000) to "c", calendar(946856085678) to "d")),
            ktConfigString(
                """
                    data:
                      '2000-01-02T01:00:00+01:00': ab
                      '2000-01-02T23:34:45Z': c
                      '2000-01-02T23:34:45.678Z': d
                """.trimIndent()
            )
        )
    }

    @Test
    fun uuid_string_map() {
        data class Data(val data: Map<UUID, String>)

        val uuid = UUID.randomUUID()
        assertEquals(
            Data(mapOf(uuid to "ab")),
            ktConfigString(
                """
                    data:
                      $uuid: ab
                      c: ignore
                """.trimIndent()
            )
        )
    }

    enum class EnumValue {
        Value1
    }

    @Test
    fun enum_string_map() {
        data class Data(val data: Map<EnumValue, String>)

        assertEquals(
            Data(mapOf(EnumValue.Value1 to "ab")),
            ktConfigString(
                """
                    data:
                      Value1: ab
                      c: ignore
                """.trimIndent()
            )
        )
    }

    @Test
    fun string_string_map_list() {
        data class Data(val data: List<Map<String, String>>)

        assertEquals(
            Data(
                listOf(
                    mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
                    mapOf("g" to "h"),
                    mapOf(),
                    mapOf("null" to "i"),
                    mapOf("j" to "k")
                )
            ),
            ktConfigString(
                """
                    data:
                      - a: b
                        c: d
                        null: e
                        5: f
                      - g: h
                      - {}
                      - 'null': i
                      - j: k
                """.trimIndent()
            )
        )
    }
}
