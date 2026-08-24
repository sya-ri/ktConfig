import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test
import kotlin.test.assertEquals

@KtConfig
@Comment("Header comment", "Second line in header")
data class HeaderCommentConfig(
    val value: String,
)

@KtConfig
data class PropertyCommentConfig(
    @Comment("Property comment", "Second line in property")
    val value: String,
)

@KtConfig
@Comment("Header comment", "Second line in header")
data class HeaderAndPropertyCommentConfig(
    @Comment("Property comment", "Second line in property")
    val value: String,
)

@KtConfig
data class NestedCommentConfig(
    val nested: Nested,
) {
    @KtConfig
    data class Nested(
        @Comment("Nested property comment", "Second line in nested property")
        val value: String,
    )
}

class CommentTest {
    @Test
    fun testGeneratedLoaderWritesHeaderComment() =
        run {
            assertEquals(
                """
                # Header comment
                # Second line in header
                
                value: value
                
                """.trimIndent(),
                HeaderCommentConfigLoader.saveToString(HeaderCommentConfig("value")),
            )
        }

    @Test
    fun testGeneratedLoaderWritesPropertyComment() =
        run {
            assertEquals(
                """
                # Property comment
                # Second line in property
                value: value
                
                """.trimIndent(),
                PropertyCommentConfigLoader.saveToString(PropertyCommentConfig("value")),
            )
        }

    @Test
    fun testGeneratedLoaderWritesHeaderAndPropertyComments() =
        run {
            assertEquals(
                """
                # Header comment
                # Second line in header
                
                # Property comment
                # Second line in property
                value: value
                
                """.trimIndent(),
                HeaderAndPropertyCommentConfigLoader.saveToString(HeaderAndPropertyCommentConfig("value")),
            )
        }

    @Test
    fun testGeneratedLoaderWritesNestedPropertyComment() =
        run {
            assertEquals(
                """
                nested:
                  # Nested property comment
                  # Second line in nested property
                  value: value
                
                """.trimIndent(),
                NestedCommentConfigLoader.saveToString(NestedCommentConfig(NestedCommentConfig.Nested("value"))),
            )
        }
}
