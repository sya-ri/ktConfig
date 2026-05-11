package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias UShortRoundTrip = RoundTripConfig<UShort>

@OptIn(ExperimentalUnsignedTypes::class)
class UShortRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            UShortRoundTrip(
                value = 123U,
                nullable = null,
                list = listOf(1U, 2U),
                nullableList = listOf(null, 3U),
                set = setOf(1U, 2U),
                arrayDeque = ArrayDeque(listOf(1U, 2U)),
                map = mapOf("key" to 4U),
                nullableMap = mapOf("key" to 5U),
            ),
            UShortRoundTripLoader::saveToString,
            UShortRoundTripLoader::loadFromString,
        )
}
