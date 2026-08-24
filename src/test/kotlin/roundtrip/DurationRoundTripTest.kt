package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.Duration
import kotlin.test.Test

@KtConfig
typealias DurationRoundTrip = RoundTripConfig<Duration>

class DurationRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = Duration.parse("PT1H2M3S")
        val b = Duration.parse("PT4H5M6S")
        assertRoundTrip(
            DurationRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            DurationRoundTripLoader::saveToString,
            DurationRoundTripLoader::loadFromString,
        )
    }
}
