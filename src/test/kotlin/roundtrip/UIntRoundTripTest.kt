package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias UIntRoundTrip = RoundTripConfig<UInt>

@OptIn(ExperimentalUnsignedTypes::class)
class UIntRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            UIntRoundTrip(
                value = 12345U,
                nullable = null,
                list = listOf(1U, 2U),
                nullableList = listOf(null, 3U),
                set = setOf(1U, 2U),
                arrayDeque = ArrayDeque(listOf(1U, 2U)),
                map = mapOf("key" to 4U),
                nullableMap = mapOf("key" to 5U),
            ),
            UIntRoundTripLoader::saveToString,
            UIntRoundTripLoader::loadFromString,
        )
}
