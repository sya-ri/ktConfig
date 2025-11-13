package serializer

import dev.s7a.ktconfig.serializer.UShortArraySerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class UShortArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            UShortArray(0),
            UShortArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            ushortArrayOf(42u),
            UShortArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            ushortArrayOf(1u, 2u, 3u, UShort.MAX_VALUE),
            UShortArraySerializer,
        )
}
