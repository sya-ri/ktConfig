
import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.ktConfigString
import org.bukkit.Location
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypeDeserializeTest {
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
        data class Data(val data: Int?)

        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(5), ktConfigString("data: '5'"))
        assertEquals(Data(5), ktConfigString("data: 5.8"))
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: 2147483647"))
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: 2147483648")) // overflow
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: -2147483648"))
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: -2147483649")) // underflow
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: '2147483647'"))
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: '2147483648'")) // overflow
        assertEquals(Data(Int.MIN_VALUE), ktConfigString("data: '-2147483648'"))
        assertEquals(Data(Int.MAX_VALUE), ktConfigString("data: '-2147483649'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun uint() {
        data class Data(val data: UInt?)

        assertEquals(Data(5U), ktConfigString("data: 5"))
        assertEquals(Data(5U), ktConfigString("data: '5'"))
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: 4294967295"))
        assertEquals(Data(UInt.MIN_VALUE), ktConfigString("data: 4294967296")) // overflow
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: -1")) // underflow
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: '4294967295'"))
        assertEquals(Data(UInt.MIN_VALUE), ktConfigString("data: '4294967296'")) // overflow
        assertEquals(Data(UInt.MAX_VALUE), ktConfigString("data: '-1'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun boolean() {
        data class Data(val data: Boolean?)

        assertEquals(Data(true), ktConfigString("data: true"))
        assertEquals(Data(true), ktConfigString("data: 'true'"))
        assertEquals(Data(null), ktConfigString("data: tr"))
        assertEquals(Data(null), ktConfigString("data: 1"))
    }

    @Test
    fun double() {
        data class Data(val data: Double?)

        assertEquals(Data(1.2), ktConfigString("data: 1.2"))
        assertEquals(Data(1.2), ktConfigString("data: '1.2'"))
        assertEquals(Data(Double.MAX_VALUE), ktConfigString("data: 1.7976931348623157E308"))
        assertEquals(Data(Double.MIN_VALUE), ktConfigString("data: 4.9E-324"))
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun float() {
        data class Data(val data: Float?)

        assertEquals(Data(1.2F), ktConfigString("data: 1.2"))
        assertEquals(Data(1.2F), ktConfigString("data: '1.2'"))
        assertEquals(Data(Float.MAX_VALUE), ktConfigString("data: 3.4028235E38"))
        assertEquals(Data(Float.MIN_VALUE), ktConfigString("data: 1.4E-45"))
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun long() {
        data class Data(val data: Long?)

        assertEquals(Data(123), ktConfigString("data: 123"))
        assertEquals(Data(123), ktConfigString("data: '123'"))
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: 9223372036854775807"))
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: 9223372036854775808")) // overflow
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: -9223372036854775808"))
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: -9223372036854775809")) // underflow
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: '9223372036854775807'"))
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: '9223372036854775808'")) // overflow
        assertEquals(Data(Long.MIN_VALUE), ktConfigString("data: '-9223372036854775808'"))
        assertEquals(Data(Long.MAX_VALUE), ktConfigString("data: '-9223372036854775809'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun ulong() {
        data class Data(val data: ULong?)

        assertEquals(Data(123U), ktConfigString("data: 123"))
        assertEquals(Data(123U), ktConfigString("data: '123'"))
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: 18446744073709551615"))
        assertEquals(Data(ULong.MIN_VALUE), ktConfigString("data: 18446744073709551616")) // overflow
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: -1")) // underflow
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: '18446744073709551615'"))
        assertEquals(Data(ULong.MIN_VALUE), ktConfigString("data: '18446744073709551616'")) // overflow
        assertEquals(Data(ULong.MAX_VALUE), ktConfigString("data: '-1'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun byte() {
        data class Data(val data: Byte?)

        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(5), ktConfigString("data: '5'"))
        assertEquals(Data(128.toByte()), ktConfigString("data: 128"))
        assertEquals(Data(0x5E), ktConfigString("data: 0x5E"))
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: 127"))
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: 128")) // overflow
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: -128"))
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: -129")) // underflow
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: '127'"))
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: '128'")) // overflow
        assertEquals(Data(Byte.MIN_VALUE), ktConfigString("data: '-128'"))
        assertEquals(Data(Byte.MAX_VALUE), ktConfigString("data: '-129'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun ubyte() {
        data class Data(val data: UByte?)

        assertEquals(Data(5U), ktConfigString("data: 5"))
        assertEquals(Data(5U), ktConfigString("data: '5'"))
        assertEquals(Data(128.toUByte()), ktConfigString("data: 128"))
        assertEquals(Data(0x5EU), ktConfigString("data: 0x5E"))
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: 255"))
        assertEquals(Data(UByte.MIN_VALUE), ktConfigString("data: 256")) // overflow
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: -1")) // underflow
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: '255'"))
        assertEquals(Data(UByte.MIN_VALUE), ktConfigString("data: '256'")) // overflow
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: '-1'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun char() {
        data class Data(val data: Char?)

        assertEquals(Data('a'), ktConfigString("data: a"))
        assertEquals(Data(49.toChar()), ktConfigString("data: 49"))
        assertEquals(Data('5'), ktConfigString("data: '5'"))
        assertEquals(Data('あ'), ktConfigString("data: あ"))
        assertEquals(Data(null), ktConfigString("data: ab"))
    }

    @Test
    fun short() {
        data class Data(val data: Short?)

        assertEquals(Data(123), ktConfigString("data: 123"))
        assertEquals(Data(123), ktConfigString("data: '123'"))
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: 32767"))
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: 32768")) // overflow
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: -32768"))
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: -32769")) // underflow
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: '32767'"))
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: '32768'")) // overflow
        assertEquals(Data(Short.MIN_VALUE), ktConfigString("data: '-32768'"))
        assertEquals(Data(Short.MAX_VALUE), ktConfigString("data: '-32769'")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun ushort() {
        data class Data(val data: UShort?)

        assertEquals(Data(123U), ktConfigString("data: 123"))
        assertEquals(Data(123U), ktConfigString("data: '123'"))
        assertEquals(Data(UShort.MAX_VALUE), ktConfigString("data: 65535"))
        assertEquals(Data(UShort.MIN_VALUE), ktConfigString("data: 65536")) // overflow
        assertEquals(Data(UShort.MAX_VALUE), ktConfigString("data: -1")) // underflow
        assertEquals(Data(UShort.MAX_VALUE), ktConfigString("data: '65535'"))
        assertEquals(Data(UShort.MIN_VALUE), ktConfigString("data: '65536'")) // overflow
        assertEquals(Data(UShort.MAX_VALUE), ktConfigString("data: -1")) // underflow
        assertEquals(Data(null), ktConfigString("data: zero"))
    }

    @Test
    fun uuid() {
        data class Data(val data: UUID?)

        val uuid = UUID.randomUUID()
        assertEquals(Data(uuid), ktConfigString("data: $uuid"))
        assertEquals(Data(null), ktConfigString("data: aaaaaaaaaaaaaaaaaaa"))
    }

    @Test
    fun location() {
        data class Data(val data: Location)

        assertEquals(
            Data(Location(null, 1.2, -5.0, 3.4, 10.5F, -42.6F)),
            ktConfigString(
                """
                    data:
                      ==: org.bukkit.Location
                      x: 1.2
                      y: -5.0
                      z: 3.4
                      yaw: 10.5
                      pitch: -42.6
                """.trimIndent()
            )
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
