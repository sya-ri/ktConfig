import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test
import kotlin.test.assertEquals

@KtConfig(hasDefault = true)
data class DefaultValueConfig(
    val value: String = "default",
)

class DefaultKtConfigTest {
    @Test
    fun testUsesDefaultValueWhenYamlIsEmpty() {
        assertEquals(DefaultValueConfig("default"), DefaultValueConfigLoader.loadFromString(""))
    }

    @Test
    fun testUsesYamlValueWhenPresent() {
        assertEquals(DefaultValueConfig("test"), DefaultValueConfigLoader.loadFromString("value: test"))
    }
}
