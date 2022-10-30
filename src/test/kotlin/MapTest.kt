import kotlin.test.Test

class MapTest {
    @Test
    fun string_string_map() {
        class Data(override val data: Map<String, String>) : TestData<Map<String, String>>

        assertMapParse<Map<String, String>, Data>(
            mapOf("a" to "b", "c" to "d", "null" to "e"),
            """
                a: b
                c: d
                null: e
            """.trimIndent()
        )
    }

    @Test
    fun nullable_string_string_map() {
        class Data(override val data: Map<String?, String>) : TestData<Map<String?, String>>

        assertMapParse<Map<String?, String>, Data>(
            mapOf("a" to "b", "c" to "d", null to "e"),
            """
                a: b
                c: d
                null: e
            """.trimIndent()
        )
    }

    @Test
    fun int_string_map() {
        class Data(override val data: Map<Int, String>) : TestData<Map<Int, String>>

        assertMapParse<Map<Int, String>, Data>(
            mapOf(0 to "ab", -1 to "c"),
            """
                0: ab
                c: ignore
                -1: c
            """.trimIndent()
        )
    }

    @Test
    fun boolean_string_map() {
        class Data(override val data: Map<Boolean, String>) : TestData<Map<Boolean, String>>

        assertMapParse<Map<Boolean, String>, Data>(
            mapOf(true to "ab", false to "c"),
            """
                true: ab
                c: ignore
                false: c
            """.trimIndent()
        )
    }

    @Test
    fun long_string_map() {
        class Data(override val data: Map<Long, String>) : TestData<Map<Long, String>>

        assertMapParse<Map<Long, String>, Data>(
            mapOf(0L to "ab", -1L to "c"),
            """
                0: ab
                c: ignore
                -1: c
            """.trimIndent()
        )
    }

    @Test
    fun byte_string_map() {
        class Data(override val data: Map<Byte, String>) : TestData<Map<Byte, String>>

        assertMapParse<Map<Byte, String>, Data>(
            mapOf(0.toByte() to "ab", (-1).toByte() to "c", 0x5E.toByte() to "d"),
            """
                0: ab
                c: ignore
                -1: c
                0x5E: d
            """.trimIndent()
        )
    }

    @Test
    fun char_string_map() {
        class Data(override val data: Map<Char, String>) : TestData<Map<Char, String>>

        assertMapParse<Map<Char, String>, Data>(
            mapOf('0' to "ab", 'd' to "e"),
            """
                0: ab
                cc: ignore
                d: e
            """.trimIndent()
        )
    }

    @Test
    fun short_string_map() {
        class Data(override val data: Map<Short, String>) : TestData<Map<Short, String>>

        assertMapParse<Map<Short, String>, Data>(
            mapOf(0.toShort() to "ab", (-1).toShort() to "c"),
            """
                0: ab
                c: ignore
                -1: c
            """.trimIndent()
        )
    }
}
