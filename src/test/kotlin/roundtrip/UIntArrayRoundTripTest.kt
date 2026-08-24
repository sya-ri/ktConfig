package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
@KtConfig
data class UIntArrayRoundTrip(
    val value: UIntArray,
)

@OptIn(ExperimentalUnsignedTypes::class)
class UIntArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertUIntArrayRoundTrip(
            uintArrayOf(1u, 2u, UInt.MAX_VALUE),
            UIntArrayRoundTripLoader::saveToString,
            UIntArrayRoundTripLoader::loadFromString,
        )
}
