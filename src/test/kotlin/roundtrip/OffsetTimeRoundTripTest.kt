package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.OffsetTime
import kotlin.test.Test

@KtConfig
typealias OffsetTimeRoundTrip = RoundTripConfig<OffsetTime>

class OffsetTimeRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = OffsetTime.parse("12:34:56+09:00")
        val b = OffsetTime.parse("01:23:45Z")
        assertRoundTrip(
            OffsetTimeRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
            OffsetTimeRoundTripLoader::saveToString,
            OffsetTimeRoundTripLoader::loadFromString,
        )
    }
}
