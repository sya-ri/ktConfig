package serializer

import dev.s7a.ktconfig.serializer.ByteSerializer
import testSerializer
import kotlin.test.Test

class ByteSerializerTest {
    @Test
    fun testZero() = testSerializer(0.toByte(), ByteSerializer)

    @Test
    fun testPositive() = testSerializer(127.toByte(), ByteSerializer)

    @Test
    fun testNegative() = testSerializer((-128).toByte(), ByteSerializer)

    @Test
    fun testFromInt() = testSerializer(42.toByte(), ByteSerializer)
}
