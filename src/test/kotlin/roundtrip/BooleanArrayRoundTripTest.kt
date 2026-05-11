package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class BooleanArrayRoundTrip(
    val value: BooleanArray,
)

class BooleanArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertBooleanArrayRoundTrip(
            booleanArrayOf(true, false),
            BooleanArrayRoundTripLoader::saveToString,
            BooleanArrayRoundTripLoader::loadFromString,
        )
}
