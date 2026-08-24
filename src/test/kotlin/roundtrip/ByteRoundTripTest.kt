package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias ByteRoundTrip = RoundTripConfig<Byte>

class ByteRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            ByteRoundTrip(
                value = 12,
                nullable = null,
                list = listOf(1, 2),
                nullableList = listOf(null, 3),
                set = setOf(1, 2),
                arrayDeque = ArrayDeque(listOf(1, 2)),
                map = mapOf("key" to 4),
                nullableMap = mapOf("key" to 5),
            ),
            ByteRoundTripLoader::saveToString,
            ByteRoundTripLoader::loadFromString,
        )
}
