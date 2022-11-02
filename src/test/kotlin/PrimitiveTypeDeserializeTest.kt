
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.ktConfigString
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PrimitiveTypeDeserializeTest {
    @Test
    fun string() {
        class Data(val data: String)

        assertEquals("hello", ktConfigString<Data>("data: hello")?.data)
        assertEquals("5", ktConfigString<Data>("data: 5")?.data)
        // unquoted null is not string, throw InvocationTargetException
        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: null")
        }
        // quoted null is string
        assertEquals("null", ktConfigString<Data>("data: 'null'")?.data)
    }

    @Test
    fun optional_string() {
        data class Data(val value: String, val optional: String = "abc")

        assertEquals(Data("a", "abc"), ktConfigString("", Data("a")))
        assertEquals(
            Data("a", "abc"),
            ktConfigString(
                """
                    value: a
                """.trimIndent()
            )
        )
        assertEquals(
            Data("a", "abc"),
            ktConfigString(
                """
                    value: a
                    ignore: value
                """.trimIndent()
            )
        )
        assertEquals(
            Data("a", "b"),
            ktConfigString(
                """
                    value: a
                    optional: b
                """.trimIndent()
            )
        )
        assertEquals(
            Data("a", "abc"),
            ktConfigString(
                """
                    value: a
                    optional: null
                """.trimIndent()
            )
        )
        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>(
                """
                    value: null
                """.trimIndent()
            )
        }
    }

    @Test
    fun nullable_string() {
        class Data(val data: String?)

        assertNull(ktConfigString<Data>("data: null")?.data)
    }

    @Test
    fun int() {
        class Data(val data: Int)

        assertEquals(5, ktConfigString<Data>("data: 5")?.data)
        assertEquals(5, ktConfigString<Data>("data: '5'")?.data)
        assertEquals(5, ktConfigString<Data>("data: 5.8")?.data)
        assertEquals(Int.MAX_VALUE, ktConfigString<Data>("data: 2147483647")?.data)
        assertEquals(Int.MIN_VALUE, ktConfigString<Data>("data: 2147483648")?.data) // overflow
        assertEquals(Int.MIN_VALUE, ktConfigString<Data>("data: -2147483648")?.data)
        assertEquals(Int.MAX_VALUE, ktConfigString<Data>("data: -2147483649")?.data) // underflow
    }

    @Test
    fun uint() {
        class Data(val data: UInt)

        assertEquals(5U, ktConfigString<Data>("data: 5")?.data)
        assertEquals(5U, ktConfigString<Data>("data: '5'")?.data)
        assertEquals(UInt.MAX_VALUE, ktConfigString<Data>("data: 4294967295")?.data)
        assertEquals(UInt.MIN_VALUE, ktConfigString<Data>("data: 4294967296")?.data) // overflow
        assertEquals(UInt.MAX_VALUE, ktConfigString<Data>("data: -1")?.data) // underflow
    }

    @Test
    fun boolean() {
        class Data(val data: Boolean)

        assertEquals(true, ktConfigString<Data>("data: true")?.data)
    }

    @Test
    fun double() {
        class Data(val data: Double)

        assertEquals(1.2, ktConfigString<Data>("data: 1.2")?.data)
        assertEquals(1.2, ktConfigString<Data>("data: '1.2'")?.data)
        assertEquals(Double.MAX_VALUE, ktConfigString<Data>("data: 1.7976931348623157E308")?.data)
        assertEquals(Double.MIN_VALUE, ktConfigString<Data>("data: 4.9E-324")?.data)
    }

    @Test
    fun float() {
        class Data(val data: Float)

        assertEquals(1.2F, ktConfigString<Data>("data: 1.2")?.data)
        assertEquals(1.2F, ktConfigString<Data>("data: '1.2'")?.data)
        assertEquals(Float.MAX_VALUE, ktConfigString<Data>("data: 3.4028235E38")?.data)
        assertEquals(Float.MIN_VALUE, ktConfigString<Data>("data: 1.4E-45")?.data)
    }

    @Test
    fun long() {
        class Data(val data: Long)

        assertEquals(123, ktConfigString<Data>("data: 123")?.data)
        assertEquals(123, ktConfigString<Data>("data: '123'")?.data)
        assertEquals(Long.MAX_VALUE, ktConfigString<Data>("data: 9223372036854775807")?.data)
        assertEquals(Long.MIN_VALUE, ktConfigString<Data>("data: 9223372036854775808")?.data) // overflow
        assertEquals(Long.MIN_VALUE, ktConfigString<Data>("data: -9223372036854775808")?.data)
        assertEquals(Long.MAX_VALUE, ktConfigString<Data>("data: -9223372036854775809")?.data) // underflow
    }

    @Test
    fun ulong() {
        class Data(val data: ULong)

        assertEquals(123U, ktConfigString<Data>("data: 123")?.data)
        assertEquals(123U, ktConfigString<Data>("data: '123'")?.data)
        assertEquals(ULong.MAX_VALUE, ktConfigString<Data>("data: '18446744073709551615'")?.data)
        assertEquals(ULong.MAX_VALUE, ktConfigString<Data>("data: -1")?.data) // underflow
    }

    @Test
    fun nullable_ulong() {
        class Data(val data: ULong?)

        assertNull(ktConfigString<Data>("data: '18446744073709551616'")?.data) // overflow: toULongOrNull returns null
    }

    @Test
    fun byte() {
        class Data(val data: Byte)

        assertEquals(5, ktConfigString<Data>("data: 5")?.data)
        assertEquals(5, ktConfigString<Data>("data: '5'")?.data)
        assertEquals(128.toByte(), ktConfigString<Data>("data: 128")?.data)
        assertEquals(0x5E, ktConfigString<Data>("data: 0x5E")?.data)
        assertEquals(Byte.MAX_VALUE, ktConfigString<Data>("data: 127")?.data)
        assertEquals(Byte.MIN_VALUE, ktConfigString<Data>("data: 128")?.data) // overflow
        assertEquals(Byte.MIN_VALUE, ktConfigString<Data>("data: -128")?.data)
        assertEquals(Byte.MAX_VALUE, ktConfigString<Data>("data: -129")?.data) // underflow
    }

    @Test
    fun ubyte() {
        class Data(val data: UByte)

        assertEquals(5U, ktConfigString<Data>("data: 5")?.data)
        assertEquals(5U, ktConfigString<Data>("data: '5'")?.data)
        assertEquals(128.toUByte(), ktConfigString<Data>("data: 128")?.data)
        assertEquals(0x5EU, ktConfigString<Data>("data: 0x5E")?.data)
        assertEquals(UByte.MAX_VALUE, ktConfigString<Data>("data: 255")?.data)
        assertEquals(UByte.MIN_VALUE, ktConfigString<Data>("data: 256")?.data) // overflow
        assertEquals(UByte.MAX_VALUE, ktConfigString<Data>("data: -1")?.data) // underflow
    }

    @Test
    fun char() {
        class Data(val data: Char)

        assertEquals('a', ktConfigString<Data>("data: a")?.data)
        assertEquals(49.toChar(), ktConfigString<Data>("data: 49")?.data)
        assertEquals('5', ktConfigString<Data>("data: '5'")?.data)
        assertEquals('あ', ktConfigString<Data>("data: あ")?.data)
        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: ab")
        }
    }

    @Test
    fun nullable_char() {
        class Data(val data: Char?)

        assertNull(ktConfigString<Data>("data: ab")?.data)
    }

    @Test
    fun short() {
        class Data(val data: Short)

        assertEquals(123, ktConfigString<Data>("data: 123")?.data)
        assertEquals(123, ktConfigString<Data>("data: '123'")?.data)
        assertEquals(Short.MAX_VALUE, ktConfigString<Data>("data: 32767")?.data)
        assertEquals(Short.MIN_VALUE, ktConfigString<Data>("data: 32768")?.data) // overflow
        assertEquals(Short.MIN_VALUE, ktConfigString<Data>("data: -32768")?.data)
        assertEquals(Short.MAX_VALUE, ktConfigString<Data>("data: -32769")?.data) // underflow
    }

    @Test
    fun ushort() {
        class Data(val data: UShort)

        assertEquals(123U, ktConfigString<Data>("data: 123")?.data)
        assertEquals(123U, ktConfigString<Data>("data: '123'")?.data)
        assertEquals(UShort.MAX_VALUE, ktConfigString<Data>("data: 65535")?.data)
        assertEquals(UShort.MIN_VALUE, ktConfigString<Data>("data: 65536")?.data) // overflow
    }

    @Test
    fun uuid() {
        class Data(val data: UUID)

        val uuid = UUID.randomUUID()
        assertEquals(uuid, ktConfigString<Data>("data: $uuid")?.data)
    }

    @Test
    fun string_list() {
        class Data(val data: List<String>)

        assertEquals(listOf("a", "bc", "def", "bc"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun int_list() {
        class Data(val data: List<Int>)

        assertEquals(listOf(1, 20, 31), ktConfigString<Data>("data: [1, 20, 31]")?.data)
    }

    @Test
    fun uint_list() {
        class Data(val data: List<UInt>)

        assertEquals(listOf(1U, UInt.MAX_VALUE, UInt.MIN_VALUE), ktConfigString<Data>("data: [1, ${UInt.MAX_VALUE}, ${UInt.MIN_VALUE}]")?.data)
    }

    @Test
    fun boolean_list() {
        class Data(val data: List<Boolean>)

        assertEquals(listOf(true, false, true), ktConfigString<Data>("data: [true, false, true]")?.data)
    }

    @Test
    fun double_list() {
        class Data(val data: List<Double>)

        assertEquals(listOf(0.5, 1.0, 32.5), ktConfigString<Data>("data: [.5, 1, 32.5]")?.data)
    }

    @Test
    fun float_list() {
        class Data(val data: List<Float>)

        assertEquals(listOf(0.5F, 1.0F, 32.5F), ktConfigString<Data>("data: [.5, 1, 32.5]")?.data)
    }

    @Test
    fun long_list() {
        class Data(val data: List<Long>)

        assertEquals(listOf<Long>(1, 20, 31), ktConfigString<Data>("data: [1, 20, 31]")?.data)
    }

    @Test
    fun ulong_list() {
        class Data(val data: List<ULong>)

        assertEquals(listOf(1U, ULong.MAX_VALUE, ULong.MIN_VALUE), ktConfigString<Data>("data: [1, ${ULong.MAX_VALUE}, ${ULong.MIN_VALUE}]")?.data)
    }

    @Test
    fun byte_list() {
        class Data(val data: List<Byte>)

        assertEquals(listOf(5, 128.toByte(), 0x5E), ktConfigString<Data>("data: [5, 128, 0x5E]")?.data)
    }

    @Test
    fun ubyte_list() {
        class Data(val data: List<UByte>)

        assertEquals(listOf(5U, 128.toUByte(), 0x5EU, UByte.MAX_VALUE), ktConfigString<Data>("data: [5, 128, 0x5E, ${UByte.MAX_VALUE}]")?.data)
    }

    @Test
    fun char_list() {
        class Data(val data: List<Char>)

        assertEquals(listOf('a', 'B', '5'), ktConfigString<Data>("data: [a, B, '5']")?.data)
    }

    @Test
    fun short_list() {
        class Data(val data: List<Short>)

        assertEquals(listOf<Short>(1, 20, 31), ktConfigString<Data>("data: [1, 20, 31]")?.data)
    }

    @Test
    fun ushort_list() {
        class Data(val data: List<UShort>)

        assertEquals(listOf(1U, 20U, 31U, UShort.MAX_VALUE), ktConfigString<Data>("data: [1, 20, 31, ${UShort.MAX_VALUE}]")?.data)
    }

    @Test
    fun string_list_list() {
        class Data(val data: List<List<String>>)

        assertEquals(listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi")), ktConfigString<Data>("data: [[a, bc, def], [g], [], [hi]]")?.data)
    }

    @Test
    fun string_list_list_list() {
        class Data(val data: List<List<List<String>>>)

        assertEquals(
            listOf(
                listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi")),
                listOf(),
                listOf(listOf("j")),
                listOf(listOf("k"), listOf("i"))
            ),
            ktConfigString<Data>("data: [[[a, bc, def], [g], [], [hi]], [], [[j]], [[k], [i]]]")?.data
        )
    }

    @JvmInline
    value class InlineData(val data: String)

    @Test
    fun inline_string() {
        assertEquals("hello", ktConfigString<InlineData>("data: hello")?.data)
    }

    enum class EnumValue {
        Value1
    }

    @Test
    fun enum() {
        class Data(val data: EnumValue?)

        assertEquals(EnumValue.Value1, ktConfigString<Data>("data: Value1")?.data)
        assertNull(ktConfigString<Data>("data: value1")?.data)
    }
}
