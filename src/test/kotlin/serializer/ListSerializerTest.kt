package serializer

import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.ListSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import kotlin.test.Test
import testSerializer

class ListSerializerTest {
    @Test
    fun testEmpty() = testSerializer(
        emptyList<String>(),
        ListSerializer(StringSerializer),
    )

    @Test
    fun testSingle() = testSerializer(
        listOf("hello"),
        ListSerializer(StringSerializer),
    )

    @Test
    fun testMultiple() = testSerializer(
        listOf("a", "b", "c"),
        ListSerializer(StringSerializer),
    )

    @Test
    fun testIntList() = testSerializer(
        listOf(1, 2, 3),
        ListSerializer(IntSerializer),
    )

    @Test
    fun testNested() = testSerializer(
        listOf(listOf(1, 2), listOf(3, 4)),
        ListSerializer(ListSerializer(IntSerializer)),
    )
}

