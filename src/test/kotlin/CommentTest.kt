import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class CommentTest {
    @Test
    fun header() {
        @Comment("comment")
        data class Data(val data: String)

        assertEquals(
            """
                # comment
                
                data: hello
                
            """.trimIndent(),
            saveKtConfigString(Data("hello"))
        )
    }
}
