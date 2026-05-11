package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias DoubleRoundTrip = RoundTripConfig<Double>

class DoubleRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            DoubleRoundTrip(
                value = 1.25,
                nullable = null,
                list = listOf(1.0, 2.0),
                nullableList = listOf(null, 3.0),
                set = setOf(1.0, 2.0),
                arrayDeque = ArrayDeque(listOf(1.0, 2.0)),
                map = mapOf("key" to 4.0),
                nullableMap = mapOf("key" to 5.0),
            ),
            DoubleRoundTripLoader::saveToString,
            DoubleRoundTripLoader::loadFromString,
        )
}
