package serializer

import dev.s7a.ktconfig.serializer.ULongArraySerializer
import testSerializer
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class ULongArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            ULongArray(0),
            ULongArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            ulongArrayOf(42uL),
            ULongArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            ulongArrayOf(1uL, 2uL, 3uL, ULong.MAX_VALUE),
            ULongArraySerializer,
        )
}
