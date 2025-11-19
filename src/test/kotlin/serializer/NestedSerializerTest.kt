package serializer

import dev.s7a.ktconfig.KtConfigLoader
import dev.s7a.ktconfig.serializer.ListSerializer
import dev.s7a.ktconfig.serializer.NestedSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration
import testConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class NestedSerializerTest {
    data class CustomData(
        val value: String,
    )

    object CustomLoader : KtConfigLoader<CustomData>() {
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

        override fun transform(value: Map<String, Any?>): CustomData =
            CustomData(
                value["value"]?.let(StringSerializer::deserialize) ?: throw IllegalArgumentException("value is null"),
            )

        override fun transformBack(value: CustomData): Map<String, Any?> =
            mapOf(
                "value" to StringSerializer.serialize(value.value),
            )
    }

    @Test
    fun testGet() {
        testConfiguration { configuration ->
            configuration.set("parent${KtConfigLoader.PATH_SEPARATOR}value", "expected")

            assertEquals(CustomData("expected"), NestedSerializer(CustomLoader).get(configuration, "parent"))
        }
    }

    @Test
    fun testSet() {
        testConfiguration { configuration ->
            NestedSerializer(CustomLoader).set(configuration, "parent", CustomData("expected"))

            assertEquals("expected", configuration.get("parent${KtConfigLoader.PATH_SEPARATOR}value"))
        }
    }

    @Test
    fun testGetList() {
        testConfiguration { configuration ->
            configuration.set("parent", listOf(mapOf("value" to "expected")))

            assertEquals(
                """
                parent:
                - value: expected
                
                """.trimIndent(),
                configuration.saveToString(),
            )

            assertEquals(listOf(CustomData("expected")), ListSerializer(NestedSerializer(CustomLoader)).get(configuration, "parent"))
        }
    }

    @Test
    fun testSetList() {
        testConfiguration { configuration ->
            ListSerializer(NestedSerializer(CustomLoader)).set(configuration, "parent", listOf(CustomData("expected")))

            assertEquals(
                """
                parent:
                - value: expected
                
                """.trimIndent(),
                configuration.saveToString(),
            )

            assertEquals(
                listOf(
                    mapOf("value" to "expected"),
                ),
                configuration.get("parent"),
            )
        }
    }
}
