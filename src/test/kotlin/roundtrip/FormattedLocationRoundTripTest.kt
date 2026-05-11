package roundtrip

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.type.FormattedLocation
import org.bukkit.Location
import kotlin.test.Test

@KtConfig
typealias FormattedLocationRoundTrip = RoundTripConfig<FormattedLocation>

class FormattedLocationRoundTripTest {
    @Test
    fun testRoundTrip() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            val a = Location(world, 1.25, 2.5, 3.75, 90F, 45F)
            val b = Location(world, 4.25, 5.5, 6.75)
            assertRoundTrip(
                FormattedLocationRoundTrip(a, null, listOf(a, b), listOf(null, a), setOf(a, b), ArrayDeque(listOf(a, b)), mapOf("key" to a), mapOf("key" to b)),
                FormattedLocationRoundTripLoader::saveToString,
                FormattedLocationRoundTripLoader::loadFromString,
            )
        }
}
