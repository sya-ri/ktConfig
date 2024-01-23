import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.ktConfigString
import serializer.StringSerializerForNesting
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingDeserializeTest {
    @Test
    fun another() {
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: Data1)

        assertEquals(
            Data2(Data1("hello")),
            ktConfigString(
                """
                data1:
                  data: h_e_l_l_o
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun another_list() {
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: List<Data1>)

        assertEquals(
            Data2(listOf(Data1("hello"))),
            ktConfigString(
                """
                data1:
                  data: h_e_l_l_o
                """.trimIndent(),
            ),
        )
        assertEquals(
            Data2(listOf(Data1("hello"))),
            ktConfigString(
                """
                data1:
                  - data: h_e_l_l_o
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun another_map() {
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: Map<String, Data1>)

        assertEquals(
            Data2(mapOf("one" to Data1("hello"))),
            ktConfigString(
                """
                data1:
                  one:
                    data: h_e_l_l_o
                """.trimIndent(),
            ),
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
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun recursive_list() {
        data class Data(val int: Int, val data: List<Data>?)

        assertEquals(
            Data(1, listOf(Data(21, listOf(Data(302, listOf(Data(4003, null))))))),
            ktConfigString(
                """
                int: 1
                data:
                - int: 21
                  data:
                  - int: 302
                    data:
                    - int: 4003
                      data: null
                """.trimIndent(),
            ),
        )
    }
}
