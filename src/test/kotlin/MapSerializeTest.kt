import dev.s7a.ktconfig.saveKtConfigString
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class MapSerializeTest {
    @Test
    fun string_string_map() {
        data class Data(val data: Map<String, String>)

        assertEquals(
            """
                data:
                  a: b
                  c: d
                  'null': e
                  '5': f
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f")))
        )
    }

    @Test
    fun nullable_string_string_map() {
        data class Data(val data: Map<String?, String>)

        assertEquals(
            """
                data:
                  a: b
                  c: d
                  null: e
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf("a" to "b", "c" to "d", null to "e")))
        )
    }

    @Test
    fun int_string_map() {
        data class Data(val data: Map<Int, String>)

        assertEquals(
            """
                data:
                  0: ab
                  -1: c
                  2147483647: d
                  -2147483648: e

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0 to "ab", -1 to "c", Int.MAX_VALUE to "d", Int.MIN_VALUE to "e")))
        )
    }

    @Test
    fun nullable_int_string_map() {
        data class Data(val data: Map<Int?, String>)

        assertEquals(
            """
                data:
                  0: ab
                  -1: c
                  null: e

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0 to "ab", -1 to "c", null to "e")))
        )
    }

    @Test
    fun uint_string_map() {
        data class Data(val data: Map<UInt, String>)

        assertEquals(
            """
                data:
                  0: ab
                  4294967295: c
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0U to "ab", UInt.MAX_VALUE to "c")))
        )
    }

    @Test
    fun boolean_string_map() {
        data class Data(val data: Map<Boolean, String>)

        assertEquals(
            """
                data:
                  true: ab
                  false: c
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(true to "ab", false to "c")))
        )
    }

    @Test
    fun float_string_map() {
        data class Data(val data: Map<Float, String>)

        assertEquals(
            """
                data:
                  0.5: ab
                  1.0: c
                  3.4028235E38: d
                  1.4E-45: e
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.5F to "ab", 1.0F to "c", Float.MAX_VALUE to "d", Float.MIN_VALUE to "e")))
        )
    }

    @Test
    fun double_string_map() {
        data class Data(val data: Map<Double, String>)

        assertEquals(
            """
                data:
                  0.5: ab
                  1.0: c
                  1.7976931348623157E308: d
                  4.9E-324: e
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.5 to "ab", 1.0 to "c", Double.MAX_VALUE to "d", Double.MIN_VALUE to "e")))
        )
    }

    @Test
    fun long_string_map() {
        data class Data(val data: Map<Long, String>)

        assertEquals(
            """
                data:
                  0: ab
                  -1: c
                  9223372036854775807: d
                  -9223372036854775808: e

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0L to "ab", -1L to "c", Long.MAX_VALUE to "d", Long.MIN_VALUE to "e")))
        )
    }

    @Test
    fun ulong_string_map() {
        data class Data(val data: Map<ULong, String>)

        assertEquals(
            """
                data:
                  0: ab
                  18446744073709551615: c
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.toULong() to "ab", ULong.MAX_VALUE to "c")))
        )
    }

    @Test
    fun byte_string_map() {
        data class Data(val data: Map<Byte, String>)

        assertEquals(
            """
                data:
                  0: ab
                  94: d
                  127: e
                  -128: f
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.toByte() to "ab", 0x5E.toByte() to "d", Byte.MAX_VALUE to "e", Byte.MIN_VALUE to "f")))
        )
    }

    @Test
    fun ubyte_string_map() {
        data class Data(val data: Map<UByte, String>)

        assertEquals(
            """
                data:
                  0: ab
                  255: c
                  94: d

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.toUByte() to "ab", UByte.MAX_VALUE to "c", 0x5E.toUByte() to "d")))
        )
    }

    @Test
    fun char_string_map() {
        data class Data(val data: Map<Char, String>)

        assertEquals(
            """
                data:
                  '0': ab
                  d: e
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf('0' to "ab", 'd' to "e")))
        )
    }

    @Test
    fun short_string_map() {
        data class Data(val data: Map<Short, String>)

        assertEquals(
            """
                data:
                  0: ab
                  -1: c
                  32767: d
                  -32768: e

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.toShort() to "ab", (-1).toShort() to "c", Short.MAX_VALUE to "d", Short.MIN_VALUE to "e")))
        )
    }

    @Test
    fun ushort_string_map() {
        data class Data(val data: Map<UShort, String>)

        assertEquals(
            """
                data:
                  0: ab
                  65535: c

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(0.toUShort() to "ab", UShort.MAX_VALUE to "c")))
        )
    }

    @Test
    fun big_integer_string_map() {
        data class Data(val data: Map<BigInteger, String>)

        assertEquals(
            """
                data:
                  0: ab
                  1000000000000000000000000000: c

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(BigInteger.ZERO to "ab", BigInteger("1000000000000000000000000000") to "c")))
        )
    }

    @Test
    fun big_decimal_string_map() {
        data class Data(val data: Map<BigDecimal, String>)

        assertEquals(
            """
                data:
                  '0': ab
                  '1000000000000000000000000000': c
                  '1.0E-100': d
                  '1.0E-1000': e

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(BigDecimal.ZERO to "ab", BigDecimal("1000000000000000000000000000") to "c", BigDecimal("1.0E-100") to "d", BigDecimal("1.0E-1000") to "e")))
        )
    }

    @Test
    fun date_string_map() {
        data class Data(val data: Map<Date, String>)

        assertEquals(
            """
                data:
                  2000-01-02T00:00:00Z: ab
                  2000-01-02T23:34:45Z: c
                  2000-01-02T23:34:45.678Z: d

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(Date(946771200000) to "ab", Date(946856085000) to "c", Date(946856085678) to "d")))
        )
    }

    @Test
    fun calendar_string_map() {
        data class Data(val data: Map<Calendar, String>)

        fun calendar(date: Long, zoneId: String): Calendar {
            return Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply { this.time = Date(date) }
        }

        assertEquals(
            """
                data:
                  2000-01-02T00:00:00Z: ab
                  2000-01-02T23:34:45Z: c
                  2000-01-02T23:34:45.678Z: d

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(calendar(946771200000, "UTC") to "ab", calendar(946856085000, "UTC") to "c", calendar(946856085678, "GMT-01:00") to "d")))
        )
    }

    @Test
    fun uuid_string_map() {
        data class Data(val data: Map<UUID, String>)

        val uuid = UUID.randomUUID()
        assertEquals(
            """
                data:
                  $uuid: ab

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(uuid to "ab")))
        )
    }

    enum class EnumValue {
        Value1
    }

    @Test
    fun enum_string_map() {
        data class Data(val data: Map<EnumValue, String>)

        assertEquals(
            """
                data:
                  Value1: ab

            """.trimIndent(),
            saveKtConfigString(Data(mapOf(EnumValue.Value1 to "ab")))
        )
    }

    @Test
    fun string_string_map_list() {
        data class Data(val data: List<Map<String, String>>)

        assertEquals(
            """
                data:
                - a: b
                  c: d
                  'null': e
                  '5': f
                - g: h
                - {}
                - 'null': i
                - j: k
                
            """.trimIndent(),
            saveKtConfigString(
                Data(
                    listOf(
                        mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
                        mapOf("g" to "h"),
                        mapOf(),
                        mapOf("null" to "i"),
                        mapOf("j" to "k")
                    )
                )
            )
        )
    }
}
