package serializer

import dev.s7a.ktconfig.serializer.UIntArraySerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class UIntArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            UIntArray(0),
            UIntArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            uintArrayOf(42u),
            UIntArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            uintArrayOf(1u, 2u, 3u, UInt.MAX_VALUE),
            UIntArraySerializer,
        )
}
