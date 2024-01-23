
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.saveKtConfigString
import serializer.StringSerializerForNesting
import kotlin.test.Test
import kotlin.test.assertEquals

class NestingSerializeTest {
    @Test
    fun another() {
        @Suppress("ktlint:standard:annotation")
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: Data1)

        assertEquals(
            """
            data1:
              data: h_e_l_l_o
            
            """.trimIndent(),
            saveKtConfigString(Data2(Data1("hello"))),
        )
    }

    @Test
    fun another_list() {
        @Suppress("ktlint:standard:annotation")
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: List<Data1>)

        assertEquals(
            """
            data1:
            - data: h_e_l_l_o
            
            """.trimIndent(),
            saveKtConfigString(Data2(listOf(Data1("hello")))),
        )
    }

    @Test
    fun another_map() {
        @Suppress("ktlint:standard:annotation")
        data class Data1(val data: @UseSerializer(StringSerializerForNesting::class) String)

        data class Data2(val data1: Map<String, Data1>)

        assertEquals(
            """
            data1:
              one:
                data: h_e_l_l_o
            
            """.trimIndent(),
            saveKtConfigString(Data2(mapOf("one" to Data1("hello")))),
        )
    }

    @Test
    fun recursive() {
        data class Data(val int: Int, val data: Data?)

        assertEquals(
            """
            data:
              data:
                int: 302
              int: 21
            int: 1
            
            """.trimIndent(),
            saveKtConfigString(
                Data(1, Data(21, Data(302, null))),
            ),
        )
    }

    @Test
    fun recursive_list() {
        data class Data(val int: Int, val data: List<Data>?)

        assertEquals(
            """
            data:
            - data:
              - data:
                - data: null
                  int: 4003
                int: 302
              int: 21
            int: 1
            
            """.trimIndent(),
            saveKtConfigString(
                Data(1, listOf(Data(21, listOf(Data(302, listOf(Data(4003, null))))))),
            ),
        )
    }
}
