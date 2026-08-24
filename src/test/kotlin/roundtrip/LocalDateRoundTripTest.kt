package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.LocalDate
import kotlin.test.Test

@KtConfig
typealias LocalDateRoundTrip = RoundTripConfig<LocalDate>

class LocalDateRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = LocalDate.of(2026, 5, 11)
        val b = LocalDate.of(2026, 5, 12)
        assertRoundTrip(
            LocalDateRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            LocalDateRoundTripLoader::saveToString,
            LocalDateRoundTripLoader::loadFromString,
        )
    }
}
