package roundtrip

import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test

@KtConfig
typealias LongRoundTrip = RoundTripConfig<Long>

class LongRoundTripTest {
    @Test
    fun testRoundTrip() =
        assertRoundTrip(
            LongRoundTrip(
                value = 123456L,
                nullable = null,
                list = listOf(1L, 2L),
                nullableList = listOf(null, 3L),
                set = setOf(1L, 2L),
                arrayDeque = ArrayDeque(listOf(1L, 2L)),
                map = mapOf("key" to 4L),
                nullableMap = mapOf("key" to 5L),
            ),
            LongRoundTripLoader::saveToString,
            LongRoundTripLoader::loadFromString,
        )
}
