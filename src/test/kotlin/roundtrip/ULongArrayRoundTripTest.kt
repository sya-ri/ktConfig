package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
@KtConfig
data class ULongArrayRoundTrip(
    val value: ULongArray,
)

@OptIn(ExperimentalUnsignedTypes::class)
class ULongArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertULongArrayRoundTrip(
            ulongArrayOf(1u, 2u, ULong.MAX_VALUE),
            ULongArrayRoundTripLoader::saveToString,
            ULongArrayRoundTripLoader::loadFromString,
        )
}
