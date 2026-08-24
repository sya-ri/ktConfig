package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class CharArrayRoundTrip(
    val value: CharArray,
)

class CharArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertCharArrayRoundTrip(
            charArrayOf('a', 'b'),
            CharArrayRoundTripLoader::saveToString,
            CharArrayRoundTripLoader::loadFromString,
        )
}
