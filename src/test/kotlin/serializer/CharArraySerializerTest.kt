package serializer

import dev.s7a.ktconfig.serializer.CharArraySerializer
import testSerializer
import kotlin.test.Test

class CharArraySerializerTest {
    @Test
    fun testEmpty() =
        testSerializer(
            CharArray(0),
            CharArraySerializer,
        )

    @Test
    fun testSingle() =
        testSerializer(
            charArrayOf('A'),
            CharArraySerializer,
        )

    @Test
    fun testMultiple() =
        testSerializer(
            charArrayOf('H', 'e', 'l', 'l', 'o'),
            CharArraySerializer,
        )
}
