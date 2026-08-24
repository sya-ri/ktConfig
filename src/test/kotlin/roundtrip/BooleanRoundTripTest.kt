package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias BooleanRoundTrip = RoundTripConfig<Boolean>

class BooleanRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            BooleanRoundTrip(
                value = true,
                nullable = null,
                list = listOf(true, false),
                nullableList = listOf(null, true),
                set = setOf(true, false),
                arrayDeque = ArrayDeque(listOf(true, false)),
                map = mapOf("key" to true),
                nullableMap = mapOf("key" to true),
            ),
            BooleanRoundTripLoader::saveToString,
            BooleanRoundTripLoader::loadFromString,
        )
}
