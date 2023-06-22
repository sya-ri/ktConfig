import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingDeserializeTest {
    @Test
    fun another() {
        data class Data1(val data: String)
        data class Data2(val data1: Data1)

        assertEquals(
            Data2(Data1("hello")),
            ktConfigString(
                """
                    data1:
                      data: hello
                """.trimIndent()
            )
        )
    }

    @Test
    fun another_map() {
        data class Data1(val data: String)
        data class Data2(val data1: Map<String, Data1>)

        assertEquals(
            Data2(mapOf("one" to Data1("hello"))),
            ktConfigString(
                """
                    data1:
                      one:
                        data: hello
                """.trimIndent()
            )
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
