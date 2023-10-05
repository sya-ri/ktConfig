import be.seeseemelk.mockbukkit.MockBukkit
import dev.s7a.ktconfig.ktConfigFile
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigFile
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KtConfigTest {
    private data class Data(val data: Int = 1)

    @BeforeTest
    fun setup() {
        MockBukkit.mock()
    }

    @AfterTest
    fun teardown() {
        MockBukkit.unmock()
    }

    @Test
    fun load_blank() {
        assertEquals(null, ktConfigString<Data>(""))
        assertEquals(Data(), ktConfigString("", Data()))
    }

    @Test
    fun load_file() {
        val directory = createTempDirectory().toFile()
        val file = directory.resolve("config.yml")
        file.writeText("data: 5")
        assertEquals(Data(5), ktConfigFile(file))
        assertEquals("data: 5", file.readText())
    }

    @Test
    fun load_file_ignore_default() {
        val directory = createTempDirectory().toFile()
        val file = directory.resolve("config.yml")
        file.writeText("data: 5")
        assertEquals(Data(5), ktConfigFile(file, Data()))
        assertEquals("data: 5", file.readText())
    }

    @Test
    fun load_file_save_default() {
        val directory = createTempDirectory().toFile()
        val file = directory.resolve("config.yml")
        assertEquals(Data(1), ktConfigFile(file, Data()))
        assertEquals("data: 1\n", file.readText())
    }

    @Test
    fun load_plugin_file() {
        val plugin = MockBukkit.createMockPlugin()
        val directory = plugin.dataFolder
        val file = directory.resolve("config.yml")
        file.writeText("data: 5")
        assertEquals(Data(5), plugin.ktConfigFile("config.yml"))
        assertEquals("data: 5", file.readText())
    }

    @Test
    fun load_plugin_file_ignore_default() {
        val plugin = MockBukkit.createMockPlugin()
        val directory = plugin.dataFolder
        val file = directory.resolve("config.yml")
        file.writeText("data: 5")
        assertEquals(Data(5), plugin.ktConfigFile("config.yml", Data()))
        assertEquals("data: 5", file.readText())
    }

    @Test
    fun load_plugin_file_save_default() {
        val plugin = MockBukkit.createMockPlugin()
        val directory = plugin.dataFolder
        val file = directory.resolve("config.yml")
        assertEquals(Data(1), plugin.ktConfigFile("config.yml", Data()))
        assertEquals("data: 1\n", file.readText())
    }

    @Test
    fun save_file() {
        val directory = createTempDirectory().toFile()
        val file = directory.resolve("config.yml")
        saveKtConfigFile(file, Data())
        assertEquals("data: 1\n", file.readText())
    }

    @Test
    fun save_plugin_file() {
        val plugin = MockBukkit.createMockPlugin()
        val directory = plugin.dataFolder
        val file = directory.resolve("config.yml")
        plugin.saveKtConfigFile("config.yml", Data())
        assertEquals("data: 1\n", file.readText())
    }

    @Test
    fun mkdirs() {
        val directory = createTempDirectory().toFile()
        val file = directory.resolve("a").resolve("b").resolve("config.yml")
        saveKtConfigFile(file, Data())
        assertEquals("data: 1\n", file.readText())
        assertEquals("a${File.separator}b${File.separator}config.yml", file.toRelativeString(directory))
    }

    @Test
    fun not_directory() {
        val directory = createTempFile().toFile()
        val file = directory.resolve("config.yml")
        assertFailsWith<FileNotFoundException> {
            saveKtConfigFile(file, Data())
        }
    }
}
