package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
data class ByteArrayRoundTrip(
    val value: ByteArray,
)

class ByteArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertByteArrayRoundTrip(
            byteArrayOf(1, 2, Byte.MAX_VALUE),
            ByteArrayRoundTripLoader::saveToString,
            ByteArrayRoundTripLoader::loadFromString,
        )
}
