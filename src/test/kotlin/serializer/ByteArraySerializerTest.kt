package serializer

import dev.s7a.ktconfig.serializer.ByteArraySerializer
import testSerializer
import kotlin.test.Test

class ByteArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            ByteArray(0),
            ByteArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            byteArrayOf(42),
            ByteArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            byteArrayOf(1, 2, 3, -128, 127),
            ByteArraySerializer,
        )
}
