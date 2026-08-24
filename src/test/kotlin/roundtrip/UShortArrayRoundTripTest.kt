package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
@KtConfig
data class UShortArrayRoundTrip(
    val value: UShortArray,
)

@OptIn(ExperimentalUnsignedTypes::class)
class UShortArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertUShortArrayRoundTrip(
            ushortArrayOf(1u, 2u, UShort.MAX_VALUE),
            UShortArrayRoundTripLoader::saveToString,
            UShortArrayRoundTripLoader::loadFromString,
        )
}
