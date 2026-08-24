package roundtrip

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.type.FormattedBlock
import kotlin.test.Test

@KtConfig
typealias FormattedBlockRoundTrip = RoundTripConfig<FormattedBlock>

class FormattedBlockRoundTripTest {
    @Test
    fun testRoundTrip() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            val a = world.getBlockAt(1, 2, 3)
            val b = world.getBlockAt(4, 5, 6)
            assertRoundTrip(
                FormattedBlockRoundTrip(
                    a,
                    null,
                    listOf(a, b),
                    listOf(null, a),
                    setOf(a, b),
                    ArrayDeque(listOf(a, b)),
                    mapOf("key" to a),
                    mapOf("key" to b),
                ),
                FormattedBlockRoundTripLoader::saveToString,
                FormattedBlockRoundTripLoader::loadFromString,
            )
        }
}
