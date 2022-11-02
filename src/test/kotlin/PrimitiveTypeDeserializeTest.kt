
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.ktConfigString
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrimitiveTypeDeserializeTest {
    @Test
    fun string() {
        data class Data(val data: String)

        assertEquals(Data("hello"), ktConfigString("data: hello"))
        assertEquals(Data("5"), ktConfigString("data: 5"))
        // unquoted null is not string, throw InvocationTargetException
        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: null")
        }
        // quoted null is string
        assertEquals(Data("null"), ktConfigString("data: 'null'"))
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
        data class Data(val data: String?)

        assertEquals(Data(null), ktConfigString("data: null"))
    }

    @Test
    fun int() {
        data class Data(val data: Int)

        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(5), ktConfigString("data: '5'"))
        assertEquals(Data(5), ktConfigString("data: 5.8"))
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: 2147483647"))
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: 2147483648")) // overflow
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: -2147483648"))
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: -2147483649")) // underflow
    }

    @Test
    fun uint() {
        data class Data(val data: UInt)

        assertEquals(Data(5U), ktConfigString("data: 5"))
        assertEquals(Data(5U), ktConfigString("data: '5'"))
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: 4294967295"))
        assertEquals(Data(UInt.MIN_VALUE), ktConfigString("data: 4294967296")) // overflow
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: -1")) // underflow
    }

    @Test
    fun boolean() {
        data class Data(val data: Boolean)

        assertEquals(Data(true), ktConfigString("data: true"))
    }

    @Test
    fun double() {
        data class Data(val data: Double)

        assertEquals(Data(1.2), ktConfigString("data: 1.2"))
        assertEquals(Data(1.2), ktConfigString("data: '1.2'"))
        assertEquals(Data(Double.MAX_VALUE), ktConfigString("data: 1.7976931348623157E308"))
        assertEquals(Data(Double.MIN_VALUE), ktConfigString("data: 4.9E-324"))
    }

    @Test
    fun float() {
        data class Data(val data: Float)

        assertEquals(Data(1.2F), ktConfigString("data: 1.2"))
        assertEquals(Data(1.2F), ktConfigString("data: '1.2'"))
        assertEquals(Data(Float.MAX_VALUE), ktConfigString("data: 3.4028235E38"))
        assertEquals(Data(Float.MIN_VALUE), ktConfigString("data: 1.4E-45"))
    }

    @Test
    fun long() {
        data class Data(val data: Long)

        assertEquals(Data(123), ktConfigString("data: 123"))
        assertEquals(Data(123), ktConfigString("data: '123'"))
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: 9223372036854775807"))
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: 9223372036854775808")) // overflow
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: -9223372036854775808"))
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: -9223372036854775809")) // underflow
    }

    @Test
    fun ulong() {
        data class Data(val data: ULong)

        assertEquals(Data(123U), ktConfigString("data: 123"))
        assertEquals(Data(123U), ktConfigString("data: '123'"))
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: '18446744073709551615'"))
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: -1")) // underflow
    }

    @Test
    fun nullable_ulong() {
        data class Data(val data: ULong?)

        assertEquals(Data(null), ktConfigString("data: '18446744073709551616'")) // overflow: toULongOrNull returns null
    }

    @Test
    fun byte() {
        data class Data(val data: Byte)

        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(5), ktConfigString("data: '5'"))
        assertEquals(Data(128.toByte()), ktConfigString("data: 128"))
        assertEquals(Data(0x5E), ktConfigString("data: 0x5E"))
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: 127"))
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: 128")) // overflow
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: -128"))
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: -129")) // underflow
    }

    @Test
    fun ubyte() {
        data class Data(val data: UByte)

        assertEquals(Data(5U), ktConfigString("data: 5"))
        assertEquals(Data(5U), ktConfigString("data: '5'"))
        assertEquals(Data(128.toUByte()), ktConfigString("data: 128"))
        assertEquals(Data(0x5EU), ktConfigString("data: 0x5E"))
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: 255"))
        assertEquals(Data(UByte.MIN_VALUE), ktConfigString("data: 256")) // overflow
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: -1")) // underflow
    }

    @Test
    fun char() {
        data class Data(val data: Char)

        assertEquals(Data('a'), ktConfigString("data: a"))
        assertEquals(Data(49.toChar()), ktConfigString("data: 49"))
        assertEquals(Data('5'), ktConfigString("data: '5'"))
        assertEquals(Data('あ'), ktConfigString("data: あ"))
        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: ab")
        }
    }

    @Test
    fun nullable_char() {
        data class Data(val data: Char?)

        assertEquals(Data(null), ktConfigString<Data>("data: ab"))
    }

    @Test
    fun short() {
        data class Data(val data: Short)

        assertEquals(Data(123), ktConfigString("data: 123"))
        assertEquals(Data(123), ktConfigString("data: '123'"))
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: 32767"))
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: 32768")) // overflow
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: -32768"))
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: -32769")) // underflow
    }

    @Test
    fun ushort() {
        data class Data(val data: UShort)

        assertEquals(Data(123U), ktConfigString("data: 123"))
        assertEquals(Data(123U), ktConfigString("data: '123'"))
        assertEquals(Data(UShort.MAX_VALUE), ktConfigString("data: 65535"))
        assertEquals(Data(UShort.MIN_VALUE), ktConfigString("data: 65536")) // overflow
    }

    @Test
    fun uuid() {
        data class Data(val data: UUID)

        val uuid = UUID.randomUUID()
        assertEquals(Data(uuid), ktConfigString("data: $uuid"))
    }

    @Test
    fun string_list() {
        data class Data(val data: List<String>)

        assertEquals(Data(listOf("a", "bc", "def", "bc")), ktConfigString("data: [a, bc, def, bc]"))
        assertEquals(Data(listOf("a")), ktConfigString("data: a"))
    }

    @Test
    fun int_list() {
        data class Data(val data: List<Int>)

        assertEquals(Data(listOf(1, 20, 31)), ktConfigString("data: [1, 20, 31]"))
    }

    @Test
    fun uint_list() {
        data class Data(val data: List<UInt>)

        assertEquals(Data(listOf(1U, UInt.MAX_VALUE, UInt.MIN_VALUE)), ktConfigString("data: [1, ${UInt.MAX_VALUE}, ${UInt.MIN_VALUE}]"))
    }

    @Test
    fun boolean_list() {
        data class Data(val data: List<Boolean>)

        assertEquals(Data(listOf(true, false, true)), ktConfigString("data: [true, false, true]"))
    }

    @Test
    fun double_list() {
        data class Data(val data: List<Double>)

        assertEquals(Data(listOf(0.5, 1.0, 32.5)), ktConfigString("data: [.5, 1, 32.5]"))
    }

    @Test
    fun float_list() {
        data class Data(val data: List<Float>)

        assertEquals(Data(listOf(0.5F, 1.0F, 32.5F)), ktConfigString("data: [.5, 1, 32.5]"))
    }

    @Test
    fun long_list() {
        data class Data(val data: List<Long>)

        assertEquals(Data(listOf<Long>(1, 20, 31)), ktConfigString("data: [1, 20, 31]"))
    }

    @Test
    fun ulong_list() {
        data class Data(val data: List<ULong>)

        assertEquals(Data(listOf(1U, ULong.MAX_VALUE, ULong.MIN_VALUE)), ktConfigString("data: [1, ${ULong.MAX_VALUE}, ${ULong.MIN_VALUE}]"))
    }

    @Test
    fun byte_list() {
        data class Data(val data: List<Byte>)

        assertEquals(Data(listOf(5, 128.toByte(), 0x5E)), ktConfigString("data: [5, 128, 0x5E]"))
    }

    @Test
    fun ubyte_list() {
        data class Data(val data: List<UByte>)

        assertEquals(Data(listOf(5U, 128.toUByte(), 0x5EU, UByte.MAX_VALUE)), ktConfigString("data: [5, 128, 0x5E, ${UByte.MAX_VALUE}]"))
    }

    @Test
    fun char_list() {
        data class Data(val data: List<Char>)

        assertEquals(Data(listOf('a', 'B', '5')), ktConfigString("data: [a, B, '5']"))
    }

    @Test
    fun short_list() {
        data class Data(val data: List<Short>)

        assertEquals(Data(listOf<Short>(1, 20, 31)), ktConfigString("data: [1, 20, 31]"))
    }

    @Test
    fun ushort_list() {
        data class Data(val data: List<UShort>)

        assertEquals(Data(listOf(1U, 20U, 31U, UShort.MAX_VALUE)), ktConfigString("data: [1, 20, 31, ${UShort.MAX_VALUE}]"))
    }

    @Test
    fun string_list_list() {
        data class Data(val data: List<List<String>>)

        assertEquals(Data(listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi"))), ktConfigString("data: [[a, bc, def], [g], [], [hi]]"))
    }

    @Test
    fun string_list_list_list() {
        data class Data(val data: List<List<List<String>>>)

        assertEquals(
            Data(
                listOf(
                    listOf(listOf("a", "bc", "def"), listOf("g"), listOf(), listOf("hi")),
                    listOf(),
                    listOf(listOf("j")),
                    listOf(listOf("k"), listOf("i"))
                )
            ),
            ktConfigString("data: [[[a, bc, def], [g], [], [hi]], [], [[j]], [[k], [i]]]")
        )
    }

    @JvmInline
    value class InlineData(val data: String)

    @Test
    fun inline_string() {
        assertEquals(InlineData("hello"), ktConfigString("data: hello"))
    }

    enum class EnumValue {
        Value1
    }

    @Test
    fun enum() {
        data class Data(val data: EnumValue?)

        assertEquals(Data(EnumValue.Value1), ktConfigString("data: Value1"))
        assertEquals(Data(null), ktConfigString("data: value1"))
    }
}
