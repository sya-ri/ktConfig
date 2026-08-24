package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias IntRoundTrip = RoundTripConfig<Int>

class IntRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            IntRoundTrip(
                value = 12345,
                nullable = null,
                list = listOf(1, 2),
                nullableList = listOf(null, 3),
                set = setOf(1, 2),
                arrayDeque = ArrayDeque(listOf(1, 2)),
                map = mapOf("key" to 4),
                nullableMap = mapOf("key" to 5),
            ),
            IntRoundTripLoader::saveToString,
            IntRoundTripLoader::loadFromString,
        )
}
