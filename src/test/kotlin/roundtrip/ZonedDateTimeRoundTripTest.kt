package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.time.ZonedDateTime
import kotlin.test.Test

@KtConfig
typealias ZonedDateTimeRoundTrip = RoundTripConfig<ZonedDateTime>

class ZonedDateTimeRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = ZonedDateTime.parse("2026-05-11T12:34:56+09:00[Asia/Tokyo]")
        val b = ZonedDateTime.parse("2026-05-12T01:23:45Z")
        assertRoundTrip(
            ZonedDateTimeRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            ZonedDateTimeRoundTripLoader::saveToString,
            ZonedDateTimeRoundTripLoader::loadFromString,
        )
    }
}
