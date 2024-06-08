package types

import utils.Data
import utils.NullableData
import utils.assertKtConfigString
import utils.assertSaveKtConfigString
import kotlin.test.Test

class CharTest {
    @Test
    fun `should get char from config`() {
        assertKtConfigString(
            Data('a'),
            """
            value: a
            """.trimIndent(),
        )
        assertKtConfigString(
            NullableData('a'),
            """
            value: a
            """.trimIndent(),
        )
    }

    @Test
    fun `should get null from config`() {
        assertKtConfigString(
            NullableData<Char>(null),
            """
            value: null
            """.trimIndent(),
        )
    }

    @Test
    fun `should get int as char from config`() {
        assertKtConfigString(
            Data(0x2),
            """
            value: 2
            """.trimIndent(),
        )
        assertKtConfigString(
            Data('2'),
            """
            value: '2'
            """.trimIndent(),
        )
    }

    @Test
    fun `should get byte array as char from config`() {
        assertKtConfigString(
            Data(2.toChar()),
            """
            value: !!binary |-
              Ag==
            """.trimIndent(),
        )
    }

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
    fun `should save char to config`() {
        assertSaveKtConfigString(
            """
            value: a
            
            """.trimIndent(),
            Data('a'),
        )
        assertSaveKtConfigString(
            """
            value: a
            
            """.trimIndent(),
            NullableData('a'),
        )
    }

    @Test
    fun `should save null to config`() {
        assertSaveKtConfigString(
            "",
            NullableData<Char>(null),
        )
    }

    @Test
    fun `should save int to config`() {
        assertSaveKtConfigString(
            """
            value: 2
            
            """.trimIndent(),
            Data(0x2),
        )
        assertSaveKtConfigString(
            """
            value: '2'
            
            """.trimIndent(),
            Data('2'),
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
