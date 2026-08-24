package type

import dev.s7a.ktconfig.type.FormattedBlockSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedBlockTest {
    @Test
    fun testDecode() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals(world.getBlockAt(1, 2, 3), FormattedBlockSerializer.decode("world, 1, 2, 3"))
        }

    @Test
    fun testEncode() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals("world, 1, 2, 3", FormattedBlockSerializer.encode(world.getBlockAt(1, 2, 3)))
        }
}
