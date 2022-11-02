import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionDeserializeTest {
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
