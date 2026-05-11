package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.Instant
import kotlin.test.Test

@KtConfig
typealias InstantRoundTrip = RoundTripConfig<Instant>

class InstantRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = Instant.parse("2026-05-11T12:34:56Z")
        val b = Instant.parse("2026-05-12T01:23:45Z")
        assertRoundTrip(
            InstantRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
            InstantRoundTripLoader::saveToString,
            InstantRoundTripLoader::loadFromString,
        )
    }
}
