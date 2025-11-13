package serializer

import dev.s7a.ktconfig.serializer.LongSerializer
import testSerializer
import kotlin.test.Test

class LongSerializerTest {
    @Test
    fun testZero() = testSerializer(0L, LongSerializer)

    @Test
    fun testPositive() = testSerializer(Long.MAX_VALUE, LongSerializer)

    @Test
    fun testNegative() = testSerializer(Long.MIN_VALUE, LongSerializer)

    @Test
    fun testNormal() = testSerializer(42L, LongSerializer)
}
