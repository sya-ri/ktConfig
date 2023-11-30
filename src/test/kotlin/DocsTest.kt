import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class DocsTest {
    private data class Data<T>(val data: T)

    @Test
    fun `support-types_string`() {
        assertEquals(Data("Hello"), ktConfigString("data: Hello"))
        assertEquals(Data("5"), ktConfigString("data: 5"))
        assertEquals(
            Data("[list1, list2]"),
            ktConfigString(
                """
                data:
                 - list1
                 - list2
                """.trimIndent(),
            ),
        )
        assertEquals(
            Data(mapOf("key1" to "value1", "key2" to "value2")),
            ktConfigString(
                """
                data:
                  key1: value1
                  key2: value2
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun `support-types_boolean`() {
        assertEquals(Data(true), ktConfigString("data: true"))
        assertEquals(Data(false), ktConfigString("data: false"))
        assertEquals(Data<Boolean?>(null), ktConfigString("data: other"))
        assertEquals(
            Data(mapOf(true to "value1", false to "value2")),
            ktConfigString(
                """
                data:
                  true: value1
                  false: value2
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun `support-types_byte`() {
        assertEquals(Data<Byte>(5), ktConfigString("data: 5"))
        assertEquals(Data<Byte>(12), ktConfigString("data: '12'"))
        assertEquals(Data<Byte>(-9), ktConfigString("data: -9"))
        assertEquals(Data<Byte>(78), ktConfigString("data: 0x4E"))
        assertEquals(Data<Byte>(-126), ktConfigString("data: 130"))
        assertEquals(
            Data(
                mapOf(
                    5.toByte() to "value1",
                    12.toByte() to "value2",
                    (-9).toByte() to "value3",
                    78.toByte() to "value4",
                    (-126).toByte() to "value5",
                ),
            ),
            ktConfigString(
                """
                data:
                  5: value1
                  '12': value2
                  -9: value3
                  0x4E: value4
                  130: value5
                """.trimIndent(),
            ),
        )
    }
}
