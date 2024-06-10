package types

import utils.Data
import utils.NullableData
import utils.assertKtConfigString
import utils.assertSaveKtConfigString
import kotlin.test.Test

class StringListTest {
    @Test
    fun `should get string as list from config`() {
        assertKtConfigString(
            Data(listOf("config string")),
            """
            value: config string
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData(listOf("config string")),
            """
            value: config string
            """.trimIndent(),
        )
    }

    @Test
    fun `should get string list (single value) from config`() {
        assertKtConfigString(
            Data(listOf("config string")),
            """
            value:
             - config string
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData(listOf("config string")),
            """
            value:
             - config string
            """.trimIndent(),
        )
    }

    @Test
    fun `should get string list (multiple values) from config`() {
        assertKtConfigString(
            Data(listOf("config string", "other string", "")),
            """
            value:
             - config string
             - other string
             - ""
            """.trimIndent(),
        )
    }

    @Test
    fun `should save string list (single value) to config`() {
        assertSaveKtConfigString(
            """
            value:
            - config string
            
            """.trimIndent(),
            Data(listOf("config string")),
        )
        assertSaveKtConfigString(
            """
            value:
            - config string
            
            """.trimIndent(),
            NullableData(listOf("config string")),
        )
    }

    @Test
    fun `should save string list (multiple values) to config`() {
        assertSaveKtConfigString(
            """
            value:
            - config string
            - other string
            - ''
            
            """.trimIndent(),
            Data(listOf("config string", "other string", "")),
        )
    }
}
