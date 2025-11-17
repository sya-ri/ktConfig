package serializer

import dev.s7a.ktconfig.serializer.ConfigurationSerializableSerializer
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import testSerializer
import kotlin.test.Test

class ConfigurationSerializableSerializerTest {
    @Test
    fun testLocation() =
        testSerializer(
            Location(null, 1.0, 2.0, 3.0, 4.0f, 5.0f),
            ConfigurationSerializableSerializer(),
            expectedYaml =
                """
                test:
                  ==: org.bukkit.Location
                  x: 1.0
                  y: 2.0
                  z: 3.0
                  pitch: 5.0
                  yaw: 4.0
                
                """.trimIndent(),
        )

    class CustomSerializable(
        val value: String,
    ) : ConfigurationSerializable {
        override fun serialize(): MutableMap<String, Any> = mutableMapOf("value" to value)

        companion object {
            @Suppress("unused")
            @JvmStatic
            fun deserialize(map: Map<String, Any>): CustomSerializable = CustomSerializable(map["value"] as String)
        }
    }

    @Test
    fun testCustom() =
        testSerializer(
            CustomSerializable("Hello"),
            ConfigurationSerializableSerializer(),
            expectedYaml =
                $$"""
                test:
                  ==: serializer.ConfigurationSerializableSerializerTest$CustomSerializable
                  value: Hello

                """.trimIndent(),
        )
}
