package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias FloatRoundTrip = RoundTripConfig<Float>

class FloatRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            FloatRoundTrip(
                value = 1.25F,
                nullable = null,
                list = listOf(1.0F, 2.0F),
                nullableList = listOf(null, 3.0F),
                set = setOf(1.0F, 2.0F),
                arrayDeque = ArrayDeque(listOf(1.0F, 2.0F)),
                map = mapOf("key" to 4.0F),
                nullableMap = mapOf("key" to 5.0F),
            ),
            FloatRoundTripLoader::saveToString,
            FloatRoundTripLoader::loadFromString,
        )
}
