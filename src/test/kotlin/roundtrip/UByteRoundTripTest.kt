package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias UByteRoundTrip = RoundTripConfig<UByte>

@OptIn(ExperimentalUnsignedTypes::class)
class UByteRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            UByteRoundTrip(
                value = 12U,
                nullable = null,
                list = listOf(1U, 2U),
                nullableList = listOf(null, 3U),
                set = setOf(1U, 2U),
                arrayDeque = ArrayDeque(listOf(1U, 2U)),
                map = mapOf("key" to 4U),
                nullableMap = mapOf("key" to 5U),
            ),
            UByteRoundTripLoader::saveToString,
            UByteRoundTripLoader::loadFromString,
        )
}
