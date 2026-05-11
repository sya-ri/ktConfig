import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.exception.NotFoundValueException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

data class GenericTypeAliasConfig<T>(
    val value: T,
    val list: List<T>,
)

@KtConfig
data class DeferredGenericTypeAliasConfig<T>(
    val value: T,
)

@KtConfig
typealias StringTypeAliasConfig = GenericTypeAliasConfig<String>

@KtConfig(loaderName = "CustomTypeAliasConfigLoader")
typealias CustomLoaderTypeAliasConfig = GenericTypeAliasConfig<Int>

@KtConfig
typealias StringDeferredGenericTypeAliasConfig = DeferredGenericTypeAliasConfig<String>

data class TypeAliasDefaultIgnoredConfig(
    val value: String = "default",
)

@KtConfig(hasDefault = true)
typealias TypeAliasDefaultIgnoredConfigAlias = TypeAliasDefaultIgnoredConfig

@KtConfig(hasDefault = true)
data class ClassDefaultTypeAliasConfig(
    val value: String = "default",
)

@KtConfig(hasDefault = false)
typealias ClassDefaultTypeAliasConfigAlias = ClassDefaultTypeAliasConfig

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

    @Test
    fun testGeneratedLoaderIsDeferredUntilGenericTypeAliasIsConcrete() {
        assertEquals(
            StringDeferredGenericTypeAliasConfig("value"),
            StringDeferredGenericTypeAliasConfigLoader.loadFromString("value: value"),
        )
    }

    @Test
    fun testTypeAliasHasDefaultIsIgnored() {
        assertFailsWith<NotFoundValueException> {
            TypeAliasDefaultIgnoredConfigAliasLoader.loadFromString("")
        }
    }

    @Test
    fun testTypeAliasLoaderUsesAliasedClassDefaultSetting() {
        assertEquals(
            ClassDefaultTypeAliasConfig("default"),
            ClassDefaultTypeAliasConfigAliasLoader.loadFromString(""),
        )
    }
}
