package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.Year
import kotlin.test.Test

@KtConfig
typealias YearRoundTrip = RoundTripConfig<Year>

class YearRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = Year.of(2026)
        val b = Year.of(2027)
        assertRoundTrip(
            YearRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
            YearRoundTripLoader::saveToString,
            YearRoundTripLoader::loadFromString,
        )
    }
}
