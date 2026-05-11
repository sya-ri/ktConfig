import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.SerialName
import kotlin.test.Test
import kotlin.test.assertTrue

@KtConfig
data class RootSerialNameConfig(
    @SerialName("path-name")
    val value: String,
)

@KtConfig
data class ListSerialNameConfig(
    val values: List<Nested>,
) {
    @KtConfig
    data class Nested(
        @SerialName("path-name")
        val value: String,
    )
}

@KtConfig
data class MapSerialNameConfig(
    val values: Map<String, Nested>,
) {
    @KtConfig
    data class Nested(
        @SerialName("path-name")
        val value: String,
    )
}

class SerialNameTest {
    @Test
    fun testGeneratedLoaderUsesSerialNameForRootProperty() =
        run {
            val yaml = RootSerialNameConfigLoader.saveToString(RootSerialNameConfig("renamed"))

            assertTrue(yaml.lines().contains("path-name: renamed"))
        }

    @Test
    fun testGeneratedLoaderUsesSerialNameForListElementProperty() =
        run {
            val yaml = ListSerialNameConfigLoader.saveToString(ListSerialNameConfig(listOf(ListSerialNameConfig.Nested("renamed"))))

            assertTrue(yaml.lines().contains("- path-name: renamed"))
        }

    @Test
    fun testGeneratedLoaderUsesSerialNameForMapValueProperty() =
        run {
            val yaml =
                MapSerialNameConfigLoader.saveToString(
                    MapSerialNameConfig(mapOf("key" to MapSerialNameConfig.Nested("renamed"))),
                )

            assertTrue(yaml.lines().contains("    path-name: renamed"))
        }
}
