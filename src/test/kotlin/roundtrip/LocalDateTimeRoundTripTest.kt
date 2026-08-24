package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.LocalDateTime
import kotlin.test.Test

@KtConfig
typealias LocalDateTimeRoundTrip = RoundTripConfig<LocalDateTime>

class LocalDateTimeRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = LocalDateTime.of(2026, 5, 11, 12, 34, 56)
        val b = LocalDateTime.of(2026, 5, 12, 1, 2, 3)
        assertRoundTrip(
            LocalDateTimeRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            LocalDateTimeRoundTripLoader::saveToString,
            LocalDateTimeRoundTripLoader::loadFromString,
        )
    }
}
