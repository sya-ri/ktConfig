import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingDeserializeTest {
    @Test
    fun another() {
        class Data1(val data: String)
        class Data2(val data1: Data1)

        assertEquals(
            "hello",
            ktConfigString<Data2>(
                """
                    data1:
                      data: hello
                """.trimIndent()
            )?.data1?.data
        )
    }

    @Test
    fun recursive() {
        data class Data(val int: Int, val data: Data?)

        assertEquals(
            Data(1, Data(21, Data(302, null))),
            ktConfigString(
                """
                    int: 1
                    data:
                      int: 21
                      data:
                        int: 302
                        data: null
                """.trimIndent()
            )
        )
    }
}
