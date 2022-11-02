
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingSerializeTest {
    @Test
    fun another() {
        data class Data1(val data: String)
        data class Data2(val data1: Data1)

        assertEquals(
            """
                data1:
                  data: hello
                
            """.trimIndent(),
            saveKtConfigString(Data2(Data1("hello")))
        )
    }

    @Test
    fun recursive() {
        data class Data(val int: Int, val data: Data?)

        assertEquals(
            """
                data:
                  data:
                    data: null
                    int: 302
                  int: 21
                int: 1
                
            """.trimIndent(),
            saveKtConfigString(
                Data(1, Data(21, Data(302, null)))
            )
        )
    }
}
