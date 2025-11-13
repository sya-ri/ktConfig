package serializer

import dev.s7a.ktconfig.serializer.UByteSerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class UByteSerializerTest {
    @Test
    fun testZero() = testSerializer(0u.toUByte(), UByteSerializer)

    @Test
    fun testMax() = testSerializer(UByte.MAX_VALUE, UByteSerializer)

    @Test
    fun testNormal() = testSerializer(42u.toUByte(), UByteSerializer)
}
