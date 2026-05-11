import dev.s7a.ktconfig.KtConfig
import kotlin.test.Test
import kotlin.test.assertEquals

data class GenericTypeAliasConfig<T>(
    val value: T,
    val list: List<T>,
)

@KtConfig
typealias StringTypeAliasConfig = GenericTypeAliasConfig<String>

@KtConfig(loaderName = "CustomTypeAliasConfigLoader")
typealias CustomLoaderTypeAliasConfig = GenericTypeAliasConfig<Int>

class TypeAliasKtConfigTest {
    @Test
    fun testGeneratedLoaderUsesTypeAliasName() {
        assertEquals(
            StringTypeAliasConfig("value", listOf("a", "b")),
            StringTypeAliasConfigLoader.loadFromString(
                """
                value: value
                list:
                - a
                - b
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderUsesGenericTypeSubstitution() {
        assertEquals(
            """
            value: value
            list:
            - a
            - b
            
            """.trimIndent(),
            StringTypeAliasConfigLoader.saveToString(StringTypeAliasConfig("value", listOf("a", "b"))),
        )
    }

    @Test
    fun testGeneratedLoaderUsesCustomLoaderName() {
        assertEquals(
            CustomLoaderTypeAliasConfig(1, listOf(2, 3)),
            CustomTypeAliasConfigLoader.loadFromString(
                """
                value: '1'
                list:
                - '2'
                - '3'
                """.trimIndent(),
            ),
        )
    }
}
