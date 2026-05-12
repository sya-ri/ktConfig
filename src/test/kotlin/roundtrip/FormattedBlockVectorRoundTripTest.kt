package roundtrip

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.type.FormattedBlockVector
import org.bukkit.util.BlockVector
import kotlin.test.Test

@KtConfig
typealias FormattedBlockVectorRoundTrip = RoundTripConfig<FormattedBlockVector>

class FormattedBlockVectorRoundTripTest {
    @Test
    fun testRoundTrip() {
        val a = BlockVector(1.25, 2.5, 3.75)
        val b = BlockVector(4.25, 5.5, 6.75)
        assertRoundTrip(
            FormattedBlockVectorRoundTrip(
                a,
                null,
                listOf(a, b),
                listOf(null, a),
                setOf(a, b),
                ArrayDeque(listOf(a, b)),
                mapOf("key" to a),
                mapOf("key" to b),
            ),
            FormattedBlockVectorRoundTripLoader::saveToString,
            FormattedBlockVectorRoundTripLoader::loadFromString,
        )
    }
}
