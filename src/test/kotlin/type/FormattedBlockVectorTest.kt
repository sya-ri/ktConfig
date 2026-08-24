package type

import dev.s7a.ktconfig.type.FormattedBlockVectorSerializer
import org.bukkit.util.BlockVector
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedBlockVectorTest {
    @Test
    fun testDecodeDecimalCoordinates() {
        assertEquals(BlockVector(1.25, 2.5, 3.75), FormattedBlockVectorSerializer.decode("1.25, 2.5, 3.75"))
    }

    @Test
    fun testEncode() {
        assertEquals("1.25,2.5,3.75", FormattedBlockVectorSerializer.encode(BlockVector(1.25, 2.5, 3.75)))
    }
}
