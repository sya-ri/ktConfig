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
}
