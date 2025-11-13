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
}
