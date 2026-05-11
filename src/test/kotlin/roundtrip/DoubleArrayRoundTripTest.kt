package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class DoubleArrayRoundTrip(
    val value: DoubleArray,
)

class DoubleArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertDoubleArrayRoundTrip(
            doubleArrayOf(1.25, 2.5),
            DoubleArrayRoundTripLoader::saveToString,
            DoubleArrayRoundTripLoader::loadFromString,
        )
}
