import dev.s7a.ktconfig.ktConfigString
import org.bukkit.Location
import kotlin.test.Test
import kotlin.test.assertEquals

class BukkitDeserializeTest {
    @Test
    fun location() {
        class Data(val data: Location)

        assertEquals(
            Location(null, 1.2, -5.0, 3.4, 10.5F, -42.6F),
            ktConfigString<Data>(
                """
                    data:
                      ==: org.bukkit.Location
                      x: 1.2
                      y: -5.0
                      z: 3.4
                      yaw: 10.5
                      pitch: -42.6
                """.trimIndent()
            )?.data
        )
    }
}
