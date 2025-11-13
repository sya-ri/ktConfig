package serializer

import dev.s7a.ktconfig.serializer.FloatSerializer
import testSerializer
import kotlin.test.Test

class FloatSerializerTest {
    @Test
    fun testZero() = testSerializer(0.0f, FloatSerializer)

    @Test
    fun testPositive() = testSerializer(Float.MAX_VALUE, FloatSerializer)

    @Test
    fun testNegative() = testSerializer(Float.MIN_VALUE, FloatSerializer)

    @Test
    fun testNormal() = testSerializer(3.14f, FloatSerializer)

    @Test
    fun testInfinity() = testSerializer(Float.POSITIVE_INFINITY, FloatSerializer)

    @Test
    fun testNegativeInfinity() = testSerializer(Float.NEGATIVE_INFINITY, FloatSerializer)

    @Test
    fun testNaN() = testSerializer(Float.NaN, FloatSerializer)
}
