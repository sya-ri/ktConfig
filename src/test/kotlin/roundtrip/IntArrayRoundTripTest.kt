package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class IntArrayRoundTrip(
    val value: IntArray,
)

class IntArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertIntArrayRoundTrip(
            intArrayOf(1, 2, Int.MAX_VALUE),
            IntArrayRoundTripLoader::saveToString,
            IntArrayRoundTripLoader::loadFromString,
        )
}
