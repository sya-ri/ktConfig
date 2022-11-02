import dev.s7a.ktconfig.ktConfigString
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
