package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias ShortRoundTrip = RoundTripConfig<Short>

class ShortRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            ShortRoundTrip(
                value = 123,
                nullable = null,
                list = listOf(1, 2),
                nullableList = listOf(null, 3),
                set = setOf(1, 2),
                arrayDeque = ArrayDeque(listOf(1, 2)),
                map = mapOf("key" to 4),
                nullableMap = mapOf("key" to 5),
            ),
            ShortRoundTripLoader::saveToString,
            ShortRoundTripLoader::loadFromString,
        )
}
