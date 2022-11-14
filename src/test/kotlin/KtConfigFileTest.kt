import be.seeseemelk.mockbukkit.MockBukkit
import dev.s7a.ktconfig.ktConfigFile
import dev.s7a.ktconfig.saveKtConfigFile
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KtConfigFileTest {
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
}
