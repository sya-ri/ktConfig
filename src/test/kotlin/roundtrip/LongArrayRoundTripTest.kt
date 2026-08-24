package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class LongArrayRoundTrip(
    val value: LongArray,
)

class LongArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertLongArrayRoundTrip(
            longArrayOf(1, 2, Long.MAX_VALUE),
            LongArrayRoundTripLoader::saveToString,
            LongArrayRoundTripLoader::loadFromString,
        )
}
