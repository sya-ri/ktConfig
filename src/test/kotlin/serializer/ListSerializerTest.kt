package serializer

import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.ListSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import testSerializer
import kotlin.test.Test

class ListSerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            emptyList<String>(),
            ListSerializer(StringSerializer),
            expectedYaml =
                """
                test: []
                
                """.trimIndent(),
        )

    @Test
    fun testNull() =
        testSerializer(
            listOf("null"),
            ListSerializer(StringSerializer),
            expectedYaml =
                """
                test:
                - 'null'

                """.trimIndent(),
        )

    @Test
    fun testSingle() =
        testSerializer(
            listOf("hello"),
            ListSerializer(StringSerializer),
        )

    @Test
    fun testMultiple() =
        testSerializer(
            listOf("a", "b", "c"),
            ListSerializer(StringSerializer),
        )

    @Test
    fun testIntList() =
        testSerializer(
            listOf(1, 2, 3),
            ListSerializer(IntSerializer),
            expectedYaml =
                """
                test:
                - '1'
                - '2'
                - '3'
                
                """.trimIndent(),
        )

    @Test
    fun testNested() =
        testSerializer(
            listOf(listOf(1, 2), listOf(3, 4)),
            ListSerializer(ListSerializer(IntSerializer)),
        )

    @Test
    fun testNullableElements() =
        testSerializer(
            listOf("hello", null, "world", "null"),
            ListSerializer.Nullable(StringSerializer),
            expectedYaml =
                """
                test:
                - hello
                - null
                - world
                - 'null'
                
                """.trimIndent(),
        )

    @Test
    fun testNestedNullable() =
        testSerializer(
            listOf(listOf("a", null), listOf(null, "b")),
            ListSerializer(ListSerializer.Nullable(StringSerializer)),
            expectedYaml =
                """
                test:
                - - a
                  - null
                - - null
                  - b
                
                """.trimIndent(),
        )

    @Test
    fun testNullableNested() =
        testSerializer(
            listOf(listOf("hello", "world"), null, listOf("test")),
            ListSerializer.Nullable(ListSerializer(StringSerializer)),
            expectedYaml =
                """
                test:
                - - hello
                  - world
                - null
                - - test
                
                """.trimIndent(),
        )
}
