package roundtrip

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.type.FormattedVector
import org.bukkit.util.Vector
import kotlin.test.Test

@KtConfig
typealias FormattedVectorRoundTrip = RoundTripConfig<FormattedVector>

class FormattedVectorRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = Vector(1.25, 2.5, 3.75)
        val b = Vector(4.25, 5.5, 6.75)
        assertRoundTrip(
            FormattedVectorRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            FormattedVectorRoundTripLoader::saveToString,
            FormattedVectorRoundTripLoader::loadFromString,
        )
    }
}
