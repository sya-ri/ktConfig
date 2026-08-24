import dev.s7a.ktconfig.Comment
import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.KtConfigError
import dev.s7a.ktconfig.KtConfigResult
import dev.s7a.ktconfig.fabric.YamlConfiguration
import dev.s7a.ktconfig.serializer.MapSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@KtConfig
@Comment("Shared header")
data class FabricConfig(
    @Comment("Server name")
    val name: String,
    val ports: List<Int>,
    val weights: Map<String, Double?>,
)

@KtConfig
data class FabricNestedConfig(
    val database: Database,
) {
    @KtConfig
    data class Database(
        @Comment("Database host")
        val host: String,
    )
}

class FabricKtConfigTest {
    private val value =
        FabricConfig(
            "lobby",
            listOf(25565, 25566),
            mapOf("normal" to 1.0, "version.2" to 2.0, "disabled" to null),
        )

    @Test
    fun roundTripGeneratedLoader() {
        val yaml = FabricConfigLoader.saveToString(value)
        assertEquals(value, FabricConfigLoader.loadFromString(yaml), yaml)
        assertTrue(yaml.contains("Shared header"), yaml)
        assertTrue(yaml.contains("Server name"), yaml)
    }

    @Test
    fun readsBukkitCompatibleYaml() {
        val yaml =
            """
            name: lobby
            ports:
            - 25565
            - 25566
            weights:
              normal: 1.0
              version.2: 2.0
              disabled: null
            """.trimIndent()
        assertEquals(value, FabricConfigLoader.loadFromString(yaml))
    }

    @Test
    fun reportsMalformedYaml() {
        val result = FabricConfigLoader.loadResultFromString("name: [")
        assertIs<KtConfigResult.Failure>(result)
        assertEquals(KtConfigError.Kind.InvalidFormat, result.errors.single().kind)
    }

    @Test
    fun collectsMissingValuesThroughSharedLoader() {
        val result = assertIs<KtConfigResult.Failure>(FabricConfigLoader.loadResultFromString(""))

        assertEquals(listOf("name", "ports", "weights"), result.errors.map(KtConfigError::path))
    }

    @Test
    fun handlesNestedConfigurationWithComments() {
        val value = FabricNestedConfig(FabricNestedConfig.Database("localhost"))
        val yaml = FabricNestedConfigLoader.saveToString(value)

        assertEquals(value, FabricNestedConfigLoader.loadFromString(yaml), yaml)
        assertTrue(yaml.contains("Database host"), yaml)
    }

    @Test
    fun savesAndLoadsFileInMissingParentDirectory() {
        val file = File(createTempDirectory().toFile(), "nested/config.yml")

        FabricConfigLoader.save(file, value)

        assertTrue(file.exists())
        assertEquals(value, FabricConfigLoader.load(file))
    }

    @Test
    fun distinguishesExplicitNullFromMissingPath() {
        val configuration = YamlConfiguration().apply { loadFromString("value: null") }

        assertTrue(configuration.contains("value"))
        assertNull(configuration.get("value"))
        assertFalse(configuration.contains("missing"))
        assertNull(configuration.get("missing"))
    }

    @Test
    fun usesConfiguredPathSeparator() {
        val configuration = YamlConfiguration('|')

        configuration.set("parent|child", "value")

        assertEquals(mapOf("child" to "value"), configuration.get("parent"))
        assertEquals("value", configuration.get("parent|child"))
    }

    @Test
    fun deserializesConfigurateMapNode() {
        val configuration =
            YamlConfiguration().apply {
                set("values", mapOf("first" to "one", "second" to "two"))
            }

        assertEquals(
            mapOf("first" to "one", "second" to "two"),
            MapSerializer(StringSerializer, StringSerializer).deserialize(configuration.root.node("values")),
        )
    }
}
