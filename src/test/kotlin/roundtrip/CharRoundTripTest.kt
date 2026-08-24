package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias CharRoundTrip = RoundTripConfig<Char>

class CharRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            CharRoundTrip(
                value = 'K',
                nullable = null,
                list = listOf('a', 'b'),
                nullableList = listOf(null, 'c'),
                set = setOf('a', 'b'),
                arrayDeque = ArrayDeque(listOf('a', 'b')),
                map = mapOf("key" to 'd'),
                nullableMap = mapOf("key" to 'e'),
            ),
            CharRoundTripLoader::saveToString,
            CharRoundTripLoader::loadFromString,
        )
}
