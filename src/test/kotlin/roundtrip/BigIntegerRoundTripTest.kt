package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.math.BigInteger
import kotlin.test.Test

@KtConfig
typealias BigIntegerRoundTrip = RoundTripConfig<BigInteger>

class BigIntegerRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = BigInteger("123456789")
        val b = BigInteger("987654321")
        assertRoundTrip(
            BigIntegerRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            BigIntegerRoundTripLoader::saveToString,
            BigIntegerRoundTripLoader::loadFromString,
        )
    }
}
