package type

import dev.s7a.ktconfig.type.FormattedVectorSerializer
import org.bukkit.util.Vector
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedVectorTest {
    @Test
    fun testDecode() {
        assertEquals(Vector(1.25, 2.5, 3.75), FormattedVectorSerializer.decode("1.25, 2.5, 3.75"))
    }

    @Test
    fun testEncode() {
        assertEquals("1.25, 2.5, 3.75", FormattedVectorSerializer.encode(Vector(1.25, 2.5, 3.75)))
    }
}
