package serializer

import dev.s7a.ktconfig.serializer.UIntSerializer
import kotlin.test.Test
import testSerializer

@OptIn(ExperimentalUnsignedTypes::class)
class UIntSerializerTest {
    @Test
    fun testZero() = testSerializer(0u, UIntSerializer)

    @Test
    fun testMax() = testSerializer(UInt.MAX_VALUE, UIntSerializer)

    @Test
    fun testNormal() = testSerializer(42u, UIntSerializer)
}

