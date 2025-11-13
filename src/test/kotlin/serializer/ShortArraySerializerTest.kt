package serializer

import dev.s7a.ktconfig.serializer.ShortArraySerializer
import testSerializer
import kotlin.test.Test

class ShortArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            ShortArray(0),
            ShortArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            shortArrayOf(42),
            ShortArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            shortArrayOf(1, 2, 3, Short.MAX_VALUE, Short.MIN_VALUE),
            ShortArraySerializer,
        )
}
