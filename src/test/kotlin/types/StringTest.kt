package types

import utils.Data
import utils.NullableData
import utils.assertKtConfigString
import utils.assertSaveKtConfigString
import kotlin.test.Test

class StringTest {
    @Test
    fun `should get string from config`() {
        assertKtConfigString(
            Data("config string"),
            """
            value: config string
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData("config string"),
            """
            value: config string
            """.trimIndent(),
        )
    }

    @Test
    fun `should get char from config`() {
        assertKtConfigString(
            Data("a"),
            """
            value: a
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData("a"),
            """
            value: a
            """.trimIndent(),
        )
    }

    @Test
    fun `should get 'null' from config`() {
        assertKtConfigString(
            Data("null"),
            """
            value: "null"
            """.trimIndent(),
        )
    }

    @Test
    fun `should get null from config`() {
        assertKtConfigString(
            NullableData<String>(null),
            """
            value: null
            """.trimIndent(),
        )
    }

    @Test
    fun `should get int string from config`() {
        assertKtConfigString(
            Data("12"),
            """
            value: 12
            """.trimIndent(),
        )
    }

    @Test
    fun `should get byte array as string from config`() {
        assertKtConfigString(
            Data(2.toChar().toString()),
            """
            value: !!binary |-
              Ag==
            """.trimIndent(),
        )
    }

    @Test
    fun `should save string to config`() {
        assertSaveKtConfigString(
            """
            value: config string
            
            """.trimIndent(),
            Data("config string"),
        )
        assertSaveKtConfigString(
            """
            value: config string
            
            """.trimIndent(),
            NullableData("config string"),
        )
    }

    @Test
    fun `should save 'null' to config`() {
        assertSaveKtConfigString(
            """
            value: 'null'
            
            """.trimIndent(),
            Data("null"),
        )
    }

    @Test
    fun `should save null to config`() {
        assertSaveKtConfigString(
            "",
            NullableData<String>(null),
        )
    }

    @Test
    fun `should save int string to config`() {
        assertSaveKtConfigString(
            """
            value: '12'
            
            """.trimIndent(),
            Data("12"),
        )
    }

    @Test
    fun `should save byte array as string to config`() {
        assertSaveKtConfigString(
            """
            value: !!binary |-
              Ag==
            
            """.trimIndent(),
            Data(2.toChar().toString()),
        )
    }
}
