package serializer

import dev.s7a.ktconfig.serializer.DoubleArraySerializer
import testSerializer
import kotlin.test.Test

class DoubleArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            DoubleArray(0),
            DoubleArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            doubleArrayOf(3.14),
            DoubleArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            doubleArrayOf(1.0, 2.5, 3.14, Double.MAX_VALUE, Double.MIN_VALUE),
            DoubleArraySerializer,
        )
}
