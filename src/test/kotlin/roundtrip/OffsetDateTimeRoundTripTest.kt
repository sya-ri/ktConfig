package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.OffsetDateTime
import kotlin.test.Test

@KtConfig
typealias OffsetDateTimeRoundTrip = RoundTripConfig<OffsetDateTime>

class OffsetDateTimeRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = OffsetDateTime.parse("2026-05-11T12:34:56+09:00")
        val b = OffsetDateTime.parse("2026-05-12T01:23:45Z")
        assertRoundTrip(
            OffsetDateTimeRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
            OffsetDateTimeRoundTripLoader::saveToString,
            OffsetDateTimeRoundTripLoader::loadFromString,
        )
    }
}
