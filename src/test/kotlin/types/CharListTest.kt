package types

import utils.Data
import utils.NullableData
import utils.assertKtConfigString
import utils.assertSaveKtConfigString
import kotlin.test.Test

class CharListTest {
    @Test
    fun `should get char as list from config`() {
        assertKtConfigString(
            Data(listOf('a')),
            """
            value: a
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData(listOf('a')),
            """
            value: a
            """.trimIndent(),
        )
    }

    @Test
    fun `should get char list (single value) from config`() {
        assertKtConfigString(
            Data(listOf('a')),
            """
            value:
            - a
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData(listOf('a')),
            """
            value:
            - a
            """.trimIndent(),
        )
    }

    @Test
    fun `should get char list (multiple values) from config`() {
        assertKtConfigString(
            Data(listOf('a', '6', 9.toChar(), 2.toChar())),
            """
            value:
            - a
            - '6'
            - 9
            - !!binary |-
              Ag==
            """.trimIndent(),
        )
    }

    @Test
    fun `should save char list (single value) to config`() {
        assertSaveKtConfigString(
            """
            value:
            - a
            
            """.trimIndent(),
            Data(listOf('a')),
        )
        assertSaveKtConfigString(
            """
            value:
            - a
            
            """.trimIndent(),
            NullableData(listOf('a')),
        )
    }

    @Test
    fun `should save char list (multiple values) to config`() {
        assertSaveKtConfigString(
            """
            value:
            - a
            - '6'
            - "\t"
            - !!binary |-
              Ag==

            """.trimIndent(),
            Data(listOf('a', '6', 9.toChar(), 2.toChar())),
        )
    }
}
