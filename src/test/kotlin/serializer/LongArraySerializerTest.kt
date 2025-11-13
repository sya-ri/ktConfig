package serializer

import dev.s7a.ktconfig.serializer.LongArraySerializer
import testSerializer
import kotlin.test.Test

class LongArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            LongArray(0),
            LongArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            longArrayOf(42L),
            LongArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            longArrayOf(1L, 2L, 3L, Long.MAX_VALUE, Long.MIN_VALUE),
            LongArraySerializer,
        )
}
