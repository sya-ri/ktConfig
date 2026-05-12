import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.KtConfigResult
import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.format
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KtConfigLoaderTest {
    data class CustomData(
        val value: String,
    )

    open class CustomLoader : KtConfigLoader<CustomData>() {
        class HasDefault : CustomLoader() {
            override fun load(
                configuration: YamlConfiguration,
                parentPath: String,
            ) = CustomData(
                StringSerializer.get(configuration, "${parentPath}value") ?: "default",
            )
        }

        override fun load(
            configuration: YamlConfiguration,
            parentPath: String,
        ): CustomData =
            CustomData(
                StringSerializer.getOrThrow(configuration, "${parentPath}value"),
            )

        override fun save(
            configuration: YamlConfiguration,
            value: CustomData,
            parentPath: String,
        ) {
            StringSerializer.set(configuration, "${parentPath}value", value.value)
        }

        override fun decode(value: Map<String, Any?>): CustomData {
            TODO("Not yet implemented")
        }

        override fun encode(value: CustomData): Map<String, Any?> {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun testFormatPathKeepsSimplePath() {
        assertEquals("parent.child", KtConfigLoader.formatPath("parent${KtConfigLoader.PATH_SEPARATOR}child"))
    }

    @Test
    fun testFormatPathQuotesSegmentContainingDot() {
        assertEquals("parent.'2.0'", KtConfigLoader.formatPath("parent${KtConfigLoader.PATH_SEPARATOR}2.0"))
    }

    @Test
    fun testLoadFromString() {
        val loader = CustomLoader()
        val data = loader.loadFromString("value: test")
        assertEquals("test", data.value)
    }

    @Test
    fun testLoadEmptyConfig() {
        val loader = CustomLoader()
        assertFailsWith<KtConfigLoadException> {
            loader.loadFromString("")
        }.apply {
            assertEquals(
                """
                Failed to load config (1 error):
                - [value] Not found value
                """.trimIndent(),
                message,
            )
        }
    }

    @Test
    fun testLoadResultReturnsErrors() {
        val loader = CustomLoader()
        val result = loader.loadResultFromString("")

        assertEquals(
            """
            Failed to load config (1 error):
            - [value] Not found value
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors.format(),
        )
    }

    @Test
    fun testSaveToString() {
        val loader = CustomLoader()
        val data = CustomData("test")
        val yaml = loader.saveToString(data)
        assertEquals("value: test\n", yaml)
    }

    @Test
    fun testSaveAndLoadFile() {
        val loader = CustomLoader()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")
        val originalData = CustomData("test")

        loader.save(configFile, originalData)
        val loadedData = loader.load(configFile)

        assertEquals(originalData, loadedData)
    }

    @Test
    fun testLoadAndSaveFile() {
        val loader = CustomLoader.HasDefault()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")

        val expectedData = CustomData("default")
        val loadedData = loader.loadAndSave(configFile)
        assertEquals(expectedData, loadedData)
        assertTrue(configFile.exists())
    }

    @Test
    fun testLoadAndSaveFileUpdated() {
        val loader = CustomLoader.HasDefault()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")
        configFile.writeText(
            """
            value: updated
            ignore: delete this value after loadAndSave
            
            """.trimIndent(),
        )

        val expectedData = CustomData("updated")
        val loadedData = loader.loadAndSave(configFile)
        assertEquals(expectedData, loadedData)
        assertTrue(configFile.exists())
        assertEquals(
            """
            value: updated
            
            """.trimIndent(),
            configFile.readText(),
        )
    }

    @Test
    fun testLoadAndSaveFileIfNotExist() {
        val loader = CustomLoader.HasDefault()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")

        val expectedData = CustomData("default")
        val loadedData = loader.loadAndSaveIfNotExists(configFile)
        assertEquals(expectedData, loadedData)
        assertTrue(configFile.exists())
    }

    @Test
    fun testLoadAndSaveFileIfNotExistUpdated() {
        val loader = CustomLoader.HasDefault()
        val tempDir = createTempDirectory().toFile()
        val configFile = File(tempDir, "config.yml")
        configFile.writeText(
            """
            value: updated
            ignore: keep this value after loadAndSaveIfNotExists
            
            """.trimIndent(),
        )

        val expectedData = CustomData("updated")
        val loadedData = loader.loadAndSaveIfNotExists(configFile) // <- if not exists
        assertEquals(expectedData, loadedData)
        assertTrue(configFile.exists())
        assertEquals(
            """
            value: updated
            ignore: keep this value after loadAndSaveIfNotExists
            
            """.trimIndent(),
            configFile.readText(),
        )
    }
}
