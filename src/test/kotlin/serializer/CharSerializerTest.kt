package serializer

import dev.s7a.ktconfig.serializer.CharSerializer
import testSerializer
import kotlin.test.Test

class CharSerializerTest {
    @Test
    fun testLetter() = testSerializer('A', CharSerializer)

    @Test
    fun testDigit() = testSerializer('5', CharSerializer)

    @Test
    fun testSpecial() = testSerializer('@', CharSerializer)

    @Test
    fun testUnicode() = testSerializer('あ', CharSerializer)

    @Test
    fun testSpace() = testSerializer(' ', CharSerializer)
}
