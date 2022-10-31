import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class MapDeserializeTest {
    @Test
    fun string_string_map() {
        class Data(val data: Map<String, String>)

        assertEquals(
            mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
            ktConfigString<Data>(
                """
                    data:
                      a: b
                      c: d
                      null: e
                      5: f
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun nullable_string_string_map() {
        class Data(val data: Map<String?, String>)

        assertEquals(
            mapOf("a" to "b", "c" to "d", null to "e"),
            ktConfigString<Data>(
                """
                    data:
                      a: b
                      c: d
                      null: e
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun int_string_map() {
        class Data(val data: Map<Int, String>)

        assertEquals(
            mapOf(0 to "ab", -1 to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun nullable_int_string_map() {
        class Data(val data: Map<Int?, String>)

        assertEquals(
            mapOf(0 to "ab", -1 to "c", null to "e"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                      DDD: d
                      null: e
                      FFF: f
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun uint_string_map() {
        class Data(val data: Map<UInt, String>)

        assertEquals(
            mapOf(0U to "ab", UInt.MAX_VALUE to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UInt.MAX_VALUE}: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun boolean_string_map() {
        class Data(val data: Map<Boolean, String>)

        assertEquals(
            mapOf(true to "ab", false to "c"),
            ktConfigString<Data>(
                """
                    data:
                      true: ab
                      c: ignore
                      false: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun long_string_map() {
        class Data(val data: Map<Long, String>)

        assertEquals(
            mapOf(0L to "ab", -1L to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun ulong_string_map() {
        class Data(val data: Map<ULong, String>)

        assertEquals(
            mapOf(0.toULong() to "ab", ULong.MAX_VALUE to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      ${ULong.MAX_VALUE}: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun byte_string_map() {
        class Data(val data: Map<Byte, String>)

        assertEquals(
            mapOf(0.toByte() to "ab", (-1).toByte() to "c", 0x5E.toByte() to "d"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                      0x5E: d
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun ubyte_string_map() {
        class Data(val data: Map<UByte, String>)

        assertEquals(
            mapOf(0.toUByte() to "ab", UByte.MAX_VALUE to "c", 0x5E.toUByte() to "d"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UByte.MAX_VALUE}: c
                      0x5E: d
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun char_string_map() {
        class Data(val data: Map<Char, String>)

        assertEquals(
            mapOf('0' to "ab", 'd' to "e"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      cc: ignore
                      d: e
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun short_string_map() {
        class Data(val data: Map<Short, String>)

        assertEquals(
            mapOf(0.toShort() to "ab", (-1).toShort() to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      -1: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun ushort_string_map() {
        class Data(val data: Map<UShort, String>)

        assertEquals(
            mapOf(0.toUShort() to "ab", UShort.MAX_VALUE to "c"),
            ktConfigString<Data>(
                """
                    data:
                      0: ab
                      c: ignore
                      ${UShort.MAX_VALUE}: c
                """.trimIndent()
            )?.data
        )
    }

    @Test
    fun string_string_map_list() {
        class Data(val data: List<Map<String, String>>)

        assertEquals(
            listOf(
                mapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
                mapOf("g" to "h"),
                mapOf(),
                mapOf("null" to "i"),
                mapOf("j" to "k")
            ),
            ktConfigString<Data>(
                """
                    data:
                      - a: b
                        c: d
                        null: e
                        5: f
                      - g: h
                      - {}
                      - null: i
                      - j: k
                """.trimIndent()
            )?.data
        )
    }
}
