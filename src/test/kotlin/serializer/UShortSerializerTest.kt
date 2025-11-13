package serializer

import dev.s7a.ktconfig.serializer.UShortSerializer
import kotlin.test.Test
import testSerializer

@OptIn(ExperimentalUnsignedTypes::class)
class UShortSerializerTest {
    @Test
    fun testZero() = testSerializer(0u.toUShort(), UShortSerializer)

    @Test
    fun testMax() = testSerializer(UShort.MAX_VALUE, UShortSerializer)

    @Test
    fun testNormal() = testSerializer(42u.toUShort(), UShortSerializer)
}

