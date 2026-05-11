package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class FloatArrayRoundTrip(
    val value: FloatArray,
)

class FloatArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertFloatArrayRoundTrip(
            floatArrayOf(1.25F, 2.5F),
            FloatArrayRoundTripLoader::saveToString,
            FloatArrayRoundTripLoader::loadFromString,
        )
}
