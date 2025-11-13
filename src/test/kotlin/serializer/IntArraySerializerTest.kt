package serializer

import dev.s7a.ktconfig.serializer.IntArraySerializer
import testSerializer
import kotlin.test.Test

class IntArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            IntArray(0),
            IntArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            intArrayOf(42),
            IntArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            intArrayOf(1, 2, 3, Int.MAX_VALUE, Int.MIN_VALUE),
            IntArraySerializer,
        )
}
