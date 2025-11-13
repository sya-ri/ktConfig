package serializer

import dev.s7a.ktconfig.serializer.StringSerializer
import testSerializer
import kotlin.test.Test

class StringSerializerTest {
    @Test
    fun testEmptyString() = testSerializer("", StringSerializer)

    @Test
    fun testNormalString() = testSerializer("Hello, World!", StringSerializer)

    @Test
    fun testSpecialCharacters() = testSerializer("!@#$%^&*()", StringSerializer)

    @Test
    fun testNumericString() = testSerializer("12345", StringSerializer)

    @Test
    fun testUnicodeCharacters() = testSerializer("こんにちは世界", StringSerializer)

    @Test
    fun testWhitespace() = testSerializer("   spaces   tabs\t\t\tnewlines\n\n", StringSerializer)

    @Test
    fun testLongString() = testSerializer("a".repeat(1000), StringSerializer)

    @Test
    fun testNullString() = testSerializer("null", StringSerializer)
}
