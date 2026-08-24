package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.math.BigDecimal
import kotlin.test.Test

@KtConfig
typealias BigDecimalRoundTrip = RoundTripConfig<BigDecimal>

class BigDecimalRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = BigDecimal("1234.5678")
        val b = BigDecimal("9876.5432")
        assertRoundTrip(
            BigDecimalRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            BigDecimalRoundTripLoader::saveToString,
            BigDecimalRoundTripLoader::loadFromString,
        )
    }
}
