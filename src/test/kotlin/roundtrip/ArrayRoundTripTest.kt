package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias ArrayRoundTrip = ArrayRoundTripConfig<String>

class ArrayRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertArrayRoundTrip(
            ArrayRoundTrip(
                value = arrayOf("a", "b"),
                nullable = null,
                nullableElements = arrayOf(null, "value"),
            ),
            ArrayRoundTripLoader::saveToString,
            ArrayRoundTripLoader::loadFromString,
        )
}
