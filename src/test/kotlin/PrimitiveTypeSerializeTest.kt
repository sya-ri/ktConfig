
import dev.s7a.ktconfig.saveKtConfigString
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitiveTypeSerializeTest {
    @Test
    fun string() {
        data class Data(val data: String)

        assertEquals("data: hello\n", saveKtConfigString(Data("hello")))
        assertEquals("data: '5'\n", saveKtConfigString(Data("5")))
        assertEquals("data: 'null'\n", saveKtConfigString(Data("null")))
    }

    @Test
    fun optional_string() {
        data class Data(val data: String = "hello")

        assertEquals("data: hello\n", saveKtConfigString(Data()))
        assertEquals("data: other\n", saveKtConfigString(Data("other")))
    }

    @Test
    fun nullable_string() {
        data class Data(val data: String?)

        assertEquals("data: hello\n", saveKtConfigString(Data("hello")))
        assertEquals("", saveKtConfigString(Data(null)))
        assertEquals("data: 'null'\n", saveKtConfigString(Data("null")))
    }

    @Test
    fun int() {
        data class Data(val data: Int)

        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 2147483647\n", saveKtConfigString(Data(Int.MAX_VALUE)))
        assertEquals("data: -2147483648\n", saveKtConfigString(Data(Int.MIN_VALUE)))
    }

    @Test
    fun uint() {
        data class Data(val data: UInt)

        assertEquals("data: 5\n", saveKtConfigString(Data(5U)))
        assertEquals("data: 4294967295\n", saveKtConfigString(Data(UInt.MAX_VALUE)))
    }

    @Test
    fun boolean() {
        data class Data(val data: Boolean)

        assertEquals("data: true\n", saveKtConfigString(Data(true)))
        assertEquals("data: false\n", saveKtConfigString(Data(false)))
    }

    @Test
    fun double() {
        data class Data(val data: Double)

        assertEquals("data: 5.0\n", saveKtConfigString(Data(5.0)))
        assertEquals("data: 1.7976931348623157E308\n", saveKtConfigString(Data(Double.MAX_VALUE)))
        assertEquals("data: 4.9E-324\n", saveKtConfigString(Data(Double.MIN_VALUE)))
    }

    @Test
    fun float() {
        data class Data(val data: Float)

        assertEquals("data: 5.0\n", saveKtConfigString(Data(5.0F)))
        assertEquals("data: 3.4028235E38\n", saveKtConfigString(Data(Float.MAX_VALUE)))
        assertEquals("data: 1.4E-45\n", saveKtConfigString(Data(Float.MIN_VALUE)))
    }

    @Test
    fun long() {
        data class Data(val data: Long)

        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 9223372036854775807\n", saveKtConfigString(Data(Long.MAX_VALUE)))
        assertEquals("data: -9223372036854775808\n", saveKtConfigString(Data(Long.MIN_VALUE)))
    }

    @Test
    fun ulong() {
        data class Data(val data: ULong)

        assertEquals("data: 5\n", saveKtConfigString(Data(5U)))
        assertEquals("data: '18446744073709551615'\n", saveKtConfigString(Data(ULong.MAX_VALUE)))
    }

    @Test
    fun byte() {
        data class Data(val data: Byte)

        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 127\n", saveKtConfigString(Data(Byte.MAX_VALUE)))
        assertEquals("data: -128\n", saveKtConfigString(Data(Byte.MIN_VALUE)))
    }

    @Test
    fun ubyte() {
        data class Data(val data: UByte)

        assertEquals("data: 5\n", saveKtConfigString(Data(5U)))
        assertEquals("data: 255\n", saveKtConfigString(Data(UByte.MAX_VALUE)))
    }

    @Test
    fun char() {
        data class Data(val data: Char)

        assertEquals("data: a\n", saveKtConfigString(Data('a')))
        assertEquals("data: あ\n", saveKtConfigString(Data('あ')))
    }

    @Test
    fun short() {
        data class Data(val data: Short)

        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 32767\n", saveKtConfigString(Data(Short.MAX_VALUE)))
        assertEquals("data: -32768\n", saveKtConfigString(Data(Short.MIN_VALUE)))
    }

    @Test
    fun ushort() {
        data class Data(val data: UShort)

        assertEquals("data: 5\n", saveKtConfigString(Data(5U)))
        assertEquals("data: 65535\n", saveKtConfigString(Data(UShort.MAX_VALUE)))
    }

    @Test
    fun uuid() {
        data class Data(val data: UUID)

        val uuid = UUID.randomUUID()
        assertEquals("data: $uuid\n", saveKtConfigString(Data(uuid)))
    }

    @Test
    fun string_list() {
        data class Data(val data: List<String>)

        assertEquals("data: []\n", saveKtConfigString(Data(listOf())))
        assertEquals(
            """
                data:
                - a
                - bc
                - d
                - bc
                
            """.trimIndent(),
            saveKtConfigString(Data(listOf("a", "bc", "d", "bc")))
        )
    }

    @Test
    fun string_list_list() {
        data class Data(val data: List<List<String>>)

        assertEquals(
            """
                data:
                - - a
                  - bc
                  - def
                - - g
                - []
                - - hi
                
            """.trimIndent(),
            saveKtConfigString(Data(listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi"))))
        )
    }

    @Test
    fun string_list_list_list() {
        data class Data(val data: List<List<List<String>>>)

        assertEquals(
            """
                data:
                - - - a
                    - bc
                    - def
                  - - g
                  - []
                  - - hi
                - []
                - - - j
                - - - k
                  - - i
                
            """.trimIndent(),
            saveKtConfigString(
                Data(
                    listOf(
                        listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi")),
                        listOf(),
                        listOf(listOf("j")),
                        listOf(listOf("k"), listOf("i"))
                    )
                )
            )
        )
    }

    @JvmInline
    value class InlineData(val data: String)

    @Test
    fun inline_string() {
        assertEquals("data: hello\n", saveKtConfigString(InlineData("hello")))
    }

    enum class EnumValue {
        Value1
    }

    @Test
    fun enum() {
        data class Data(val data: EnumValue?)

        assertEquals("data: Value1\n", saveKtConfigString(Data(EnumValue.Value1)))
    }
}
