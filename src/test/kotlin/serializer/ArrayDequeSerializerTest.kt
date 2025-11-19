package serializer

import dev.s7a.ktconfig.serializer.ArrayDequeSerializer
import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import testSerializer
import kotlin.test.Test

class ArrayDequeSerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            ArrayDeque<String>(),
            ArrayDequeSerializer(StringSerializer),
        )

    @Test
    fun testSingle() =
        testSerializer(
            ArrayDeque(listOf("hello")),
            ArrayDequeSerializer(StringSerializer),
        )

    @Test
    fun testMultiple() =
        testSerializer(
            ArrayDeque(listOf("a", "b", "c")),
            ArrayDequeSerializer(StringSerializer),
        )

    @Test
    fun testIntArrayDeque() =
        testSerializer(
            ArrayDeque(listOf(1, 2, 3)),
            ArrayDequeSerializer(IntSerializer),
        )

    @Test
    fun testNullableElements() =
        testSerializer(
            ArrayDeque(listOf("hello", null, "world", "null")),
            ArrayDequeSerializer.Nullable(StringSerializer),
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
            ArrayDeque(listOf(ArrayDeque(listOf("a", null)), ArrayDeque(listOf(null, "b")))),
            ArrayDequeSerializer(ArrayDequeSerializer.Nullable(StringSerializer)),
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
            ArrayDeque(listOf(ArrayDeque(listOf("hello", "world")), null, ArrayDeque(listOf("test")))),
            ArrayDequeSerializer.Nullable(ArrayDequeSerializer(StringSerializer)),
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
