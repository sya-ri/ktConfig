package serializer

import dev.s7a.ktconfig.serializer.FloatArraySerializer
import testSerializer
import kotlin.test.Test

class FloatArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            FloatArray(0),
            FloatArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            floatArrayOf(3.14f),
            FloatArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            floatArrayOf(1.0f, 2.5f, 3.14f, Float.MAX_VALUE, Float.MIN_VALUE),
            FloatArraySerializer,
        )
}
