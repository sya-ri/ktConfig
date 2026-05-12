package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.YearMonth
import kotlin.test.Test

@KtConfig
typealias YearMonthRoundTrip = RoundTripConfig<YearMonth>

class YearMonthRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = YearMonth.of(2026, 5)
        val b = YearMonth.of(2027, 6)
        assertRoundTrip(
            YearMonthRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            YearMonthRoundTripLoader::saveToString,
            YearMonthRoundTripLoader::loadFromString,
        )
    }
}
