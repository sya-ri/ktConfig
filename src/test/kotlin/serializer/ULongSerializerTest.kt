package serializer

import dev.s7a.ktconfig.serializer.ULongSerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class ULongSerializerTest {
    @Test
    fun testZero() = testSerializer(0uL, ULongSerializer)

    @Test
    fun testMax() = testSerializer(ULong.MAX_VALUE, ULongSerializer)

    @Test
    fun testNormal() = testSerializer(42uL, ULongSerializer)
}
