package roundtrip

import dev.s7a.ktconfig.KtConfig
import java.util.UUID
import kotlin.test.Test

@KtConfig
typealias UUIDRoundTrip = RoundTripConfig<UUID>

class UUIDRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val b = UUID.fromString("00000000-0000-0000-0000-000000000002")
        assertRoundTrip(
            UUIDRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            UUIDRoundTripLoader::saveToString,
            UUIDRoundTripLoader::loadFromString,
        )
    }
}
