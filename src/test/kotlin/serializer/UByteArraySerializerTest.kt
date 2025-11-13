package serializer

import dev.s7a.ktconfig.serializer.UByteArraySerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class UByteArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            UByteArray(0),
            UByteArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            ubyteArrayOf(42u),
            UByteArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            ubyteArrayOf(1u, 2u, 3u, UByte.MAX_VALUE),
            UByteArraySerializer,
        )
}
