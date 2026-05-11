package roundtrip

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.type.FormattedWorld
import kotlin.test.Test

@KtConfig
typealias FormattedWorldRoundTrip = RoundTripConfig<FormattedWorld>

class FormattedWorldRoundTripTest {
    @Test
    fun testRoundTrip() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertRoundTrip(
                FormattedWorldRoundTrip(world, null, listOf(world), listOf(null, world), setOf(world), ArrayDeque(listOf(world)), mapOf("key" to world), mapOf("key" to world)),
                FormattedWorldRoundTripLoader::saveToString,
                FormattedWorldRoundTripLoader::loadFromString,
            )
        }
}
