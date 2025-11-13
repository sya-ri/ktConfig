package serializer

import dev.s7a.ktconfig.serializer.IntSerializer
import dev.s7a.ktconfig.serializer.MapSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import kotlin.test.Test
import testSerializer

class MapSerializerTest {
    @Test
    fun testEmpty() = testSerializer(
        emptyMap<String, String>(),
        MapSerializer(StringSerializer, StringSerializer),
    )

    @Test
    fun testSingle() = testSerializer(
        mapOf("key" to "value"),
        MapSerializer(StringSerializer, StringSerializer),
    )

    @Test
    fun testMultiple() = testSerializer(
        mapOf("a" to "1", "b" to "2", "c" to "3"),
        MapSerializer(StringSerializer, StringSerializer),
    )

    @Test
    fun testIntMap() = testSerializer(
        mapOf(1 to "one", 2 to "two", 3 to "three"),
        MapSerializer(IntSerializer, StringSerializer),
    )

    @Test
    fun testNested() = testSerializer(
        mapOf("a" to mapOf(1 to "one", 2 to "two")),
        MapSerializer(StringSerializer, MapSerializer(IntSerializer, StringSerializer)),
    )
}

