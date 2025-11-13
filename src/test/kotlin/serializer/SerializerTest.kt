package serializer

import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SerializerTest {
    @Test
    fun testGetSave() {
        val configuration = YamlConfiguration()
        val serializer = StringSerializer
        val path = "test"
        val value = "value"
        configuration.set(path, value)

        // get
        assertEquals(value, serializer.get(configuration, path))
        assertEquals(null, serializer.get(configuration, "invalid-path"))

        // getOrThrow
        assertEquals(value, serializer.getOrThrow(configuration, path))
        assertFailsWith<IllegalArgumentException> {
            serializer.getOrThrow(configuration, "invalid-path")
        }

        // save
        val value2 = "another value"
        serializer.set(configuration, path, value2)
        assertEquals(value2, configuration.getString(path))
    }
}
