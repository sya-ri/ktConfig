package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.Period
import kotlin.test.Test

@KtConfig
typealias PeriodRoundTrip = RoundTripConfig<Period>

class PeriodRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = Period.parse("P1Y2M3D")
        val b = Period.parse("P4Y5M6D")
        assertRoundTrip(
            PeriodRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
            PeriodRoundTripLoader::saveToString,
            PeriodRoundTripLoader::loadFromString,
        )
    }
}
