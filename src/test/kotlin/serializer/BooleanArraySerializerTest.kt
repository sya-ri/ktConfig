package serializer

import dev.s7a.ktconfig.serializer.BooleanArraySerializer
import testSerializer
import kotlin.test.Test

class BooleanArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            BooleanArray(0),
            BooleanArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            booleanArrayOf(true),
            BooleanArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            booleanArrayOf(true, false, true),
            BooleanArraySerializer,
        )
}
