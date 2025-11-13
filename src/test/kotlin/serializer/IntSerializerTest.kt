package serializer

import dev.s7a.ktconfig.serializer.IntSerializer
import testSerializer
import kotlin.test.Test

class IntSerializerTest {
    @Test
    fun testZero() = testSerializer(0, IntSerializer)

    @Test
    fun testPositive() = testSerializer(Int.MAX_VALUE, IntSerializer)

    @Test
    fun testNegative() = testSerializer(Int.MIN_VALUE, IntSerializer)

    @Test
    fun testNormal() = testSerializer(42, IntSerializer)
}

