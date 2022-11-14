import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionDeserializeTest {
    @Test
    fun string_list() {
        data class Data(val data: List<String>?)

        assertEquals(Data(listOf("a", "bc", "def", "bc")), ktConfigString("data: [a, bc, def, bc]"))
        assertEquals(Data(listOf("a")), ktConfigString("data: a"))
        assertEquals(
            Data(listOf("[a]")),
            ktConfigString(
                """
                    data:
                     - - a
                """.trimIndent()
            )
        )
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

        assertEquals(Data(listOf(1, 20, 31)), ktConfigString("data: [1, 20, 31]"))
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

        assertEquals(Data(listOf(1, 20, 31)), ktConfigString("data: [1, 20, 31]"))
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

    @Test
    fun string_mutable_list() {
        data class Data(val data: MutableList<String>)

        assertEquals(Data(mutableListOf("a", "bc", "def", "bc")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_set() {
        data class Data(val data: Set<String>)

        assertEquals(Data(setOf("a", "bc", "def")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_string_hash_map() {
        data class Data(val data: HashMap<String, String>)

        assertEquals(
            hashMapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
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
    fun string_string_linked_hash_map() {
        data class Data(val data: LinkedHashMap<String, String>)

        assertEquals(
            linkedMapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f"),
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
    fun string_hash_set() {
        data class Data(val data: HashSet<String>)

        assertEquals(Data(hashSetOf("a", "bc", "def")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_linked_hash_set() {
        data class Data(val data: LinkedHashSet<String>)

        assertEquals(Data(linkedSetOf("a", "bc", "def")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_mutable_set() {
        data class Data(val data: MutableSet<String>)

        assertEquals(Data(mutableSetOf("a", "bc", "def")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_iterable() {
        data class Data(val data: Iterable<String>)

        assertEquals(Data(listOf("a", "bc", "def", "bc")), ktConfigString("data: [a, bc, def, bc]"))
    }

    @Test
    fun string_collection() {
        data class Data(val data: Collection<String>)

        assertEquals(Data(listOf("a", "bc", "def", "bc")), ktConfigString("data: [a, bc, def, bc]"))
    }
}
