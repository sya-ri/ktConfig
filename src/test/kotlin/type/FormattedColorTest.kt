package type

import dev.s7a.ktconfig.type.FormattedColorSerializer
import org.bukkit.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattedColorTest {
    @Test
    fun testDecodeAndEncodeRgb() {
        assertEquals(Color.fromRGB(0x1F, 0x2E, 0x3D), FormattedColorSerializer.decode("#1F2E3D"))
        assertEquals("#1f2e3d", FormattedColorSerializer.encode(Color.fromRGB(0x1F, 0x2E, 0x3D)))
    }

    @Test
    fun testDecodeAndEncodeArgbWhenSupported() {
        if (FormattedColorSerializer.isSupportedAlpha) {
            assertEquals(Color.fromARGB(0x1F, 0x2E, 0x3D, 0x4C), FormattedColorSerializer.decode("#1F2E3D4C"))
            assertEquals("#1f2e3d4c", FormattedColorSerializer.encode(Color.fromARGB(0x1F, 0x2E, 0x3D, 0x4C)))
        }
    }
}
