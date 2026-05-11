package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class ShortArrayRoundTrip(
    val value: ShortArray,
)

class ShortArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertShortArrayRoundTrip(
            shortArrayOf(1, 2, Short.MAX_VALUE),
            ShortArrayRoundTripLoader::saveToString,
            ShortArrayRoundTripLoader::loadFromString,
        )
}
