
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CollectionSerializeTest {
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

    @Test
    fun string_mutable_list() {
        data class Data(val data: MutableList<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def
                - bc

            """.trimIndent(),
            saveKtConfigString(Data(mutableListOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_set() {
        data class Data(val data: Set<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def

            """.trimIndent(),
            saveKtConfigString(Data(setOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_string_hash_map() {
        data class Data(val data: HashMap<String, String>)

        assertEquals(
            """
                data:
                  a: b
                  c: d
                  'null': e
                  '5': f
                
            """.trimIndent(),
            saveKtConfigString(Data(hashMapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f")))
        )
    }

    @Test
    fun string_string_linked_hash_map() {
        data class Data(val data: LinkedHashMap<String, String>)

        assertEquals(
            """
                data:
                  a: b
                  c: d
                  'null': e
                  '5': f
                
            """.trimIndent(),
            saveKtConfigString(Data(linkedMapOf("a" to "b", "c" to "d", "null" to "e", "5" to "f")))
        )
    }

    @Test
    fun string_hash_set() {
        data class Data(val data: HashSet<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def

            """.trimIndent(),
            saveKtConfigString(Data(hashSetOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_linked_hash_set() {
        data class Data(val data: LinkedHashSet<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def

            """.trimIndent(),
            saveKtConfigString(Data(linkedSetOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_mutable_set() {
        data class Data(val data: MutableSet<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def

            """.trimIndent(),
            saveKtConfigString(Data(mutableSetOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_iterable() {
        data class Data(val data: Iterable<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def
                - bc

            """.trimIndent(),
            saveKtConfigString(Data(listOf("a", "bc", "def", "bc")))
        )
    }

    @Test
    fun string_collection() {
        data class Data(val data: Collection<String>)

        assertEquals(
            """
                data:
                - a
                - bc
                - def
                - bc

            """.trimIndent(),
            saveKtConfigString(Data(listOf("a", "bc", "def", "bc")))
        )
    }
}
