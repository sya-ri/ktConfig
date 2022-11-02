
import dev.s7a.ktconfig.saveKtConfigString
import org.bukkit.Location
import kotlin.test.Test
import kotlin.test.assertEquals

class BukkitSerializeTest {
    @Test
    fun location() {
        data class Data(val data: Location)

        assertEquals(
            """
                data:
                  ==: org.bukkit.Location
                  x: 1.2
                  y: -5.0
                  z: 3.4
                  pitch: -42.6
                  yaw: 10.5
                
            """.trimIndent(),
            saveKtConfigString(
                Data(Location(null, 1.2, -5.0, 3.4, 10.5F, -42.6F))
            )
        )
    }
}
