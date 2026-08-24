package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
@KtConfig
data class UByteArrayRoundTrip(
    val value: UByteArray,
)

@OptIn(ExperimentalUnsignedTypes::class)
class UByteArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertUByteArrayRoundTrip(
            ubyteArrayOf(1u, 2u, UByte.MAX_VALUE),
            UByteArrayRoundTripLoader::saveToString,
            UByteArrayRoundTripLoader::loadFromString,
        )
}
