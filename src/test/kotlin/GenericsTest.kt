import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericsTest {
    private data class Data<T>(val data: T)

    private data class ListData<T>(val data: List<T>)

    private data class ListListData<T>(val data: List<List<T>>)

    private data class MapData<T, U>(val data: Map<T, U>)

    @Test
    fun serialize() {
        assertEquals("data: hello\n", saveKtConfigString(Data("hello")))
        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 2.3\n", saveKtConfigString(Data(2.3)))
        assertEquals("data: 255\n", saveKtConfigString(Data(UByte.MAX_VALUE)))
        assertEquals("", saveKtConfigString(Data<Boolean?>(null)))
        assertEquals(
            """
            data:
            - hello
            
            """.trimIndent(),
            saveKtConfigString(ListData(listOf("hello"))),
        )
        assertEquals(
            """
            data:
            - 5
            
            """.trimIndent(),
            saveKtConfigString(ListData(listOf(5))),
        )
        assertEquals(
            """
            data:
            - - hello
            
            """.trimIndent(),
            saveKtConfigString(ListListData(listOf(listOf("hello")))),
        )
        assertEquals(
            """
            data:
              a: 1
              b: 2
            
            """.trimIndent(),
            saveKtConfigString(MapData(mapOf("a" to 1, "b" to 2))),
        )
    }

    @Test
    fun deserialize() {
        assertEquals(Data("hello"), ktConfigString("data: hello"))
        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(2.3), ktConfigString("data: 2.3"))
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: 255"))
        assertEquals(Data<Boolean?>(null), ktConfigString("data: other"))
        assertEquals(
            ListData(listOf("hello")),
            ktConfigString("data: hello"),
        )
        assertEquals(
            ListData(listOf("hello")),
            ktConfigString("data: [hello]"),
        )
        assertEquals(
            ListData(listOf(5)),
            ktConfigString("data: 5"),
        )
        assertEquals(
            ListData<Int>(listOf()),
            ktConfigString("data: string"),
        )
        assertEquals(
            ListData<Int?>(listOf(null)),
            ktConfigString("data: string"),
        )
        assertEquals(
            ListListData(listOf(listOf("hello"))),
            ktConfigString("data: hello"),
        )
        assertEquals(
            ListListData(listOf(listOf("hello"))),
            ktConfigString("data: [hello]"),
        )
        assertEquals(
            ListListData(listOf(listOf("hello"))),
            ktConfigString("data: [[hello]]"),
        )
        assertEquals(
            MapData(mapOf("a" to 1, "b" to 2)),
            ktConfigString("data: {'a': 1, 'b': 2}"),
        )
    }
}
