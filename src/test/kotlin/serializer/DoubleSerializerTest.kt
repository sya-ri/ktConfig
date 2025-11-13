package serializer

import dev.s7a.ktconfig.serializer.DoubleSerializer
import testSerializer
import kotlin.test.Test

class DoubleSerializerTest {
    @Test
    fun testZero() = testSerializer(0.0, DoubleSerializer)

    @Test
    fun testPositive() = testSerializer(Double.MAX_VALUE, DoubleSerializer)

    @Test
    fun testNegative() = testSerializer(Double.MIN_VALUE, DoubleSerializer)

    @Test
    fun testNormal() = testSerializer(3.14, DoubleSerializer)

    @Test
    fun testInfinity() = testSerializer(Double.POSITIVE_INFINITY, DoubleSerializer)

    @Test
    fun testNegativeInfinity() = testSerializer(Double.NEGATIVE_INFINITY, DoubleSerializer)

    @Test
    fun testNaN() = testSerializer(Double.NaN, DoubleSerializer)
}
