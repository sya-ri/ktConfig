package serializer

import dev.s7a.ktconfig.serializer.ShortSerializer
import testSerializer
import kotlin.test.Test

class ShortSerializerTest {
    @Test
    fun testZero() = testSerializer(0.toShort(), ShortSerializer)

    @Test
    fun testPositive() = testSerializer(Short.MAX_VALUE, ShortSerializer)

    @Test
    fun testNegative() = testSerializer(Short.MIN_VALUE, ShortSerializer)

    @Test
    fun testNormal() = testSerializer(42.toShort(), ShortSerializer)
}

