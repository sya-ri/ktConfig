import dev.s7a.ktconfig.PathName
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class PathNameTest {
    @Test
    fun serialize() {
        data class Data(@PathName("data1") val data: String) {
            @PathName("data2")
            val inner = "hi"
        }

        assertEquals(
            """
                data1: hello
                data2: hi
                
            """.trimIndent(),
            saveKtConfigString(Data("hello"))
        )
    }

    @Test
    fun deserialize() {
        data class Data(val data1: String, @PathName("data1") val data2: String)

        assertEquals(Data("hello", "hello"), ktConfigString("data1: hello"))
    }
}
