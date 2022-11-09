import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CommentTest {
    @Test
    fun header() {
        @Comment("line1", "line2")
        data class Data(val data: String)

        assertEquals(
            """
                # line1
                # line2
                
                data: hello
                
            """.trimIndent(),
            saveKtConfigString(Data("hello"))
        )
    }

    @Test
    fun value() {
        data class Data(
            @Comment("line1", "line2")
            val data: String
        )

        assertEquals(
            """
                # line1
                # line2
                data: hello
                
            """.trimIndent(),
            saveKtConfigString(Data("hello"))
        )
    }

    @Test
    fun nest() {
        @Comment("default")
        data class Data1(
            @Comment("line1", "line2")
            val data: String
        )

        @Comment("data2")
        data class Data2(
            @Comment("data1")
            val data1: Data1
        )

        assertEquals(
            """
                # data2
                
                # data1
                data1:
                  # line1
                  # line2
                  data: hello
                
            """.trimIndent(),
            saveKtConfigString(Data2(Data1("hello")))
        )
    }
}
