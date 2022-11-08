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
}
