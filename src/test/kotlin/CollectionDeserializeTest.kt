import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionDeserializeTest {
    @Test
    fun string_mutable_list() {
        class Data(val data: MutableList<String>)

        assertEquals(mutableListOf("a", "bc", "def", "bc"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_set() {
        class Data(val data: Set<String>)

        assertEquals(setOf("a", "bc", "def"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_string_hash_map() {
        class Data(val data: HashMap<String, String>)

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
        class Data(val data: LinkedHashMap<String, String>)

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
        class Data(val data: HashSet<String>)

        assertEquals(hashSetOf("a", "bc", "def"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_linked_hash_set() {
        class Data(val data: LinkedHashSet<String>)

        assertEquals(linkedSetOf("a", "bc", "def"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_mutable_set() {
        class Data(val data: MutableSet<String>)

        assertEquals(mutableSetOf("a", "bc", "def"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_iterable() {
        class Data(val data: Iterable<String>)

        assertEquals(listOf("a", "bc", "def", "bc"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }

    @Test
    fun string_collection() {
        class Data(val data: Collection<String>)

        assertEquals(listOf("a", "bc", "def", "bc"), ktConfigString<Data>("data: [a, bc, def, bc]")?.data)
    }
}
