package serializer

import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.SetSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import testSerializer
import kotlin.test.Test

class SetSerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            emptySet<String>(),
            SetSerializer(StringSerializer),
        )

    @Test
    fun testNull() =
        testSerializer(
            setOf("null"),
            SetSerializer(StringSerializer),
            expectedYaml =
                """
                test:
                - 'null'

                """.trimIndent(),
        )

    @Test
    fun testSingle() =
        testSerializer(
            setOf("hello"),
            SetSerializer(StringSerializer),
        )

    @Test
    fun testMultiple() =
        testSerializer(
            setOf("a", "b", "c"),
            SetSerializer(StringSerializer),
        )

    @Test
    fun testIntSet() =
        testSerializer(
            setOf(1, 2, 3),
            SetSerializer(IntSerializer),
        )

    @Test
    fun testNullableElements() =
        testSerializer(
            setOf("hello", null, "world", "null"),
            SetSerializer.Nullable(StringSerializer),
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
            setOf(setOf("a", null), setOf(null, "b")),
            SetSerializer(SetSerializer.Nullable(StringSerializer)),
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
            setOf(setOf("hello", "world"), null, setOf("test")),
            SetSerializer.Nullable(SetSerializer(StringSerializer)),
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
