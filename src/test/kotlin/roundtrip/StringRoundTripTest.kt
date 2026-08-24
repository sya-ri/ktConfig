package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias StringRoundTrip = RoundTripConfig<String>

class StringRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            StringRoundTrip(
                value = "text",
                nullable = null,
                list = listOf("a", "b"),
                nullableList = listOf(null, "value"),
                set = setOf("a", "b"),
                arrayDeque = ArrayDeque(listOf("a", "b")),
                map = mapOf("key" to "value"),
                nullableMap = mapOf("key" to "value"),
            ),
            StringRoundTripLoader::saveToString,
            StringRoundTripLoader::loadFromString,
        )
}
