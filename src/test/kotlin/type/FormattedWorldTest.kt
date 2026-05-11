package type

import dev.s7a.ktconfig.type.FormattedWorldSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedWorldTest {
    @Test
    fun testDecode() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals(world, FormattedWorldSerializer.decode("world"))
        }

    @Test
    fun testEncode() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals("world", FormattedWorldSerializer.encode(world))
        }
}
