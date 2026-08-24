package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias ULongRoundTrip = RoundTripConfig<ULong>

@OptIn(ExperimentalUnsignedTypes::class)
class ULongRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            ULongRoundTrip(
                value = 123456UL,
                nullable = null,
                list = listOf(1UL, 2UL),
                nullableList = listOf(null, 3UL),
                set = setOf(1UL, 2UL),
                arrayDeque = ArrayDeque(listOf(1UL, 2UL)),
                map = mapOf("key" to 4UL),
                nullableMap = mapOf("key" to 5UL),
            ),
            ULongRoundTripLoader::saveToString,
            ULongRoundTripLoader::loadFromString,
        )
}
