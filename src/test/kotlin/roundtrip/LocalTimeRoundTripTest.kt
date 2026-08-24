package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.LocalTime
import kotlin.test.Test

@KtConfig
typealias LocalTimeRoundTrip = RoundTripConfig<LocalTime>

class LocalTimeRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = LocalTime.of(12, 34, 56)
        val b = LocalTime.of(1, 2, 3)
        assertRoundTrip(
            LocalTimeRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            LocalTimeRoundTripLoader::saveToString,
            LocalTimeRoundTripLoader::loadFromString,
        )
    }
}
