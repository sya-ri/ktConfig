package serializer

import dev.s7a.ktconfig.serializer.ArraySerializer
import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import testSerializer
import kotlin.test.Test

class ArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            emptyArray<String>(),
            ArraySerializer(StringSerializer),
            expectedYaml =
                """
                test: []

                """.trimIndent(),
        )

    @Test
    fun testNull() =
        testSerializer(
            arrayOf("null"),
            ArraySerializer(StringSerializer),
            expectedYaml =
                """
                test:
                - 'null'

                """.trimIndent(),
        )

    @Test
    fun testSingle() =
        testSerializer(
            arrayOf("hello"),
            ArraySerializer(StringSerializer),
        )

    @Test
    fun testMultiple() =
        testSerializer(
            arrayOf("a", "b", "c"),
            ArraySerializer(StringSerializer),
        )

    @Test
    fun testIntArray() =
        testSerializer(
            arrayOf(1, 2, 3),
            ArraySerializer(IntSerializer),
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
            arrayOf(arrayOf(1, 2), arrayOf(3, 4)),
            ArraySerializer(ArraySerializer(IntSerializer)),
        )

    @Test
    fun testNullableElements() =
        testSerializer(
            arrayOf("hello", null, "world", "null"),
            ArraySerializer.Nullable(StringSerializer),
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
            arrayOf(arrayOf("a", null), arrayOf(null, "b")),
            ArraySerializer(ArraySerializer.Nullable(StringSerializer)),
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
            arrayOf(arrayOf("hello", "world"), null, arrayOf("test")),
            ArraySerializer.Nullable(ArraySerializer(StringSerializer)),
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
