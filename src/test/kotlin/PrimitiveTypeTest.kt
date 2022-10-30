
import kotlin.test.Test

class PrimitiveTypeTest {
    @Test
    fun string() {
        class Data(override val data: String) : TestData<String>

        assertParse<String, Data>("hello", "hello")
    }

    @Test
    fun int() {
        class Data(override val data: Int) : TestData<Int>

        assertParse<Int, Data>(5, "5")
    }

    @Test
    fun boolean() {
        class Data(override val data: Boolean) : TestData<Boolean>

        assertParse<Boolean, Data>(true, "true")
    }

    @Test
    fun double() {
        class Data(override val data: Double) : TestData<Double>

        assertParse<Double, Data>(1.2, "1.2")
    }

    @Test
    fun float() {
        class Data(override val data: Float) : TestData<Float>

        assertParse<Float, Data>(1.2F, "1.2")
    }

    @Test
    fun long() {
        class Data(override val data: Long) : TestData<Long>

        assertParse<Long, Data>(123, "123")
    }

    @Test
    fun byte() {
        class Data(override val data: Byte) : TestData<Byte>

        assertParse<Byte, Data>(5, "5")
        assertParse<Byte, Data>(128.toByte(), "128")
        assertParse<Byte, Data>(0x5E, "0x5E")
    }

    @Test
    fun char() {
        class Data(override val data: Char) : TestData<Char>
        class NullableData(override val data: Char?) : TestData<Char?>

        assertParse<Char, Data>('a', "a")
        assertParse<Char, Data>(49.toChar(), "49")
        assertParse<Char, Data>('5', "'5'")
        assertParse<Char, Data>('あ', "あ")
        assertParse<Char?, NullableData>(null, "")
        assertParse<Char?, NullableData>(null, "ab")
    }

    @Test
    fun short() {
        class Data(override val data: Short) : TestData<Short>

        assertParse<Short, Data>(123, "123")
    }

    @Test
    fun string_list() {
        class Data(override val data: List<String>) : TestData<List<String>>

        assertParse<List<String>, Data>(listOf("a", "bc", "def"), "[a, bc, def]")
    }

    @Test
    fun int_list() {
        class Data(override val data: List<Int>) : TestData<List<Int>>

        assertParse<List<Int>, Data>(listOf(1, 20, 31), "[1, 20, 31]")
    }

    @Test
    fun boolean_list() {
        class Data(override val data: List<Boolean>) : TestData<List<Boolean>>

        assertParse<List<Boolean>, Data>(listOf(true, false, true), "[true, false, true]")
    }

    @Test
    fun double_list() {
        class Data(override val data: List<Double>) : TestData<List<Double>>

        assertParse<List<Double>, Data>(listOf(0.5, 1.0, 32.5), "[.5, 1, 32.5]")
    }

    @Test
    fun float_list() {
        class Data(override val data: List<Float>) : TestData<List<Float>>

        assertParse<List<Float>, Data>(listOf(0.5F, 1.0F, 32.5F), "[.5, 1, 32.5]")
    }

    @Test
    fun long_list() {
        class Data(override val data: List<Long>) : TestData<List<Long>>

        assertParse<List<Long>, Data>(listOf(1, 20, 31), "[1, 20, 31]")
    }

    @Test
    fun byte_list() {
        class Data(override val data: List<Byte>) : TestData<List<Byte>>

        assertParse<List<Byte>, Data>(listOf(5, 128.toByte(), 0x5E), "[5, 128, 0x5E]")
    }

    @Test
    fun char_list() {
        class Data(override val data: List<Char>) : TestData<List<Char>>

        assertParse<List<Char>, Data>(listOf('a', 'B', '5'), "[a, B, '5']")
    }

    @Test
    fun short_list() {
        class Data(override val data: List<Short>) : TestData<List<Short>>

        assertParse<List<Short>, Data>(listOf(1, 20, 31), "[1, 20, 31]")
    }
}
