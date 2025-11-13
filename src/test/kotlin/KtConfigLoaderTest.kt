import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals

class KtConfigLoaderTest {
    data class CustomData(
        val value: String,
    )

    class CustomLoader : KtConfigLoader<CustomData>() {
        override fun load(configuration: YamlConfiguration): CustomData =
            CustomData(
                StringSerializer.getOrThrow(configuration, "value"),
            )

        override fun save(
            configuration: YamlConfiguration,
            value: CustomData,
        ) {
            StringSerializer.set(configuration, "value", value.value)
        }
    }

    @Test
    fun testLoadFromString() {
        val loader = CustomLoader()
        val data = loader.loadFromString("value: test")
        assertEquals("test", data.value)
    }

    @Test
    fun testSaveToString() {
        val loader = CustomLoader()
        val data = CustomData("test")
        val yaml = loader.saveToString(data)
        assertEquals("value: test\n", yaml)
    }

    @Test
    fun testLoadAndSaveFile() {
        val loader = CustomLoader()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")
        val originalData = CustomData("test")

        loader.save(configFile, originalData)
        val loadedData = loader.load(configFile)

        assertEquals(originalData, loadedData)
    }
}
