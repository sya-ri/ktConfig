import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericsTest {
    private data class Data<T>(val data: T)

    @Test
    fun serialize() {
        assertEquals("data: hello\n", saveKtConfigString(Data("hello")))
        assertEquals("data: 5\n", saveKtConfigString(Data(5)))
        assertEquals("data: 2.3\n", saveKtConfigString(Data(2.3)))
        assertEquals("data: 255\n", saveKtConfigString(Data(UByte.MAX_VALUE)))
        assertEquals("", saveKtConfigString(Data<Boolean?>(null)))
    }

    @Test
    fun deserialize() {
        assertEquals(Data("hello"), ktConfigString("data: hello"))
        assertEquals(Data(5), ktConfigString("data: 5"))
        assertEquals(Data(2.3), ktConfigString("data: 2.3"))
        assertEquals(Data(UByte.MAX_VALUE), ktConfigString("data: 255"))
        assertEquals(Data<Boolean?>(null), ktConfigString("data: other"))
    }
}
