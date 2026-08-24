package type

import dev.s7a.ktconfig.type.FormatedLocationSerializer
import org.bukkit.Location
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedLocationTest {
    @Test
    fun testDecodeWithoutYawAndPitch() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals(Location(world, 1.25, 2.5, 3.75), FormatedLocationSerializer.decode("world, 1.25, 2.5, 3.75"))
        }

    @Test
    fun testDecodeWithYawAndPitch() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals(Location(world, 1.25, 2.5, 3.75, 90F, 45F), FormatedLocationSerializer.decode("world, 1.25, 2.5, 3.75, 90, 45"))
        }

    @Test
    fun testEncodeWithoutYawAndPitch() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals("world, 1.25, 2.5, 3.75", FormatedLocationSerializer.encode(Location(world, 1.25, 2.5, 3.75)))
        }

    @Test
    fun testEncodeWithYawAndPitch() =
        withMockBukkit {
            val world = addSimpleWorld("world")
            assertEquals(
                "world, 1.25, 2.5, 3.75, 90.0, 45.0",
                FormatedLocationSerializer.encode(Location(world, 1.25, 2.5, 3.75, 90F, 45F)),
            )
        }
}
