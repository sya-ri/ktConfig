import kotlin.test.Test
import kotlin.test.assertEquals

class LoaderNameTest {
    @Test
    fun testNestedCustomLoaderObjectName() {
        val actual = CustomServerConfigNestedLoader.saveToString(ServerConfig.Nested(serverName = "test"))

        assertEquals(
            """
            serverName: test
            
            """.trimIndent(),
            actual,
        )
    }

    @Test
    fun testNestedCustomLoaderNameLoad() {
        val actual =
            ServerConfigLoader.loadFromString(
                """
                nested:
                  serverName: test
                
                """.trimIndent(),
            )

        assertEquals(
            ServerConfig(
                nested = ServerConfig.Nested(serverName = "test"),
            ),
            actual,
        )
    }

    @Test
    fun testNestedCustomLoaderNameSave() {
        val actual =
            ServerConfigLoader.saveToString(
                ServerConfig(
                    nested = ServerConfig.Nested(serverName = "test"),
                ),
            )

        assertEquals(
            """
            nested:
              serverName: test
            
            """.trimIndent(),
            actual,
        )
    }

    @Test
    fun testRawStringComment() {
        val actual =
            RawStringCommentConfigLoader.saveToString(
                RawStringCommentConfig(
                    updaterDelay = "1h",
                    message = "test",
                ),
            )

        assertEquals(
            listOf(
                "# Header line",
                "# Header default",
                "",
                "# The amount of time to pass until a new update check occurs.",
                "# Default: 1h",
                "updaterDelay: 1h",
                "# Line one",
                "# Line two",
                "message: test",
                "",
            ).joinToString("\n"),
            actual,
        )
    }
}
