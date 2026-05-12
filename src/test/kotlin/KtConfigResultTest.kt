import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.KtConfigError
import dev.s7a.ktconfig.KtConfigResult
import dev.s7a.ktconfig.SerialName
import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.type.FormattedVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@KtConfig
data class RequiredValuesConfig(
    val host: String,
    val port: Int,
)

@KtConfig
data class NullListValueConfig(
    val values: List<String>,
)

@KtConfig
data class InvalidFormattedVectorConfig(
    val vector: FormattedVector,
)

@KtConfig
data class UnsupportedBooleanValueConfig(
    val enabled: Boolean,
)

@KtConfig(discriminator = "type")
sealed interface InvalidDiscriminatorConfig {
    @KtConfig
    @SerialName("known")
    data class Known(
        val value: String,
    ) : InvalidDiscriminatorConfig
}

class KtConfigResultTest {
    @Test
    fun testLoadCollectsMultipleErrorsBeforeThrowing() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                RequiredValuesConfigLoader.loadFromString("")
            }

        assertEquals(
            """
            Failed to load config (2 errors):
            - [host] Not found value
            - [port] Not found value
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadExceptionMessageMatchesNullValueError() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                NullListValueConfigLoader.loadFromString(
                    """
                    values:
                      - null
                    """.trimIndent(),
                )
            }

        assertEquals(
            """
            Failed to load config (1 error):
            - [values] Must not be null
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadExceptionMessageMatchesInvalidFormatError() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                InvalidFormattedVectorConfigLoader.loadFromString("vector: invalid")
            }

        assertEquals(
            """
            Failed to load config (1 error):
            - [vector] Invalid format: invalid, expected: X, Y, Z
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadExceptionMessageMatchesInvalidDiscriminatorError() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                InvalidDiscriminatorConfigLoader.loadFromString("type: unknown")
            }

        assertEquals(
            """
            Failed to load config (1 error):
            - Invalid discriminator: unknown
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadExceptionMessageMatchesUnsupportedConvertError() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                UnsupportedBooleanValueConfigLoader.loadFromString(
                    """
                    enabled:
                      - true
                    """.trimIndent(),
                )
            }

        assertEquals(
            """
            Failed to load config (1 error):
            - [enabled] Unsupported convert: java.util.ArrayList -> kotlin.Boolean
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testDecodeCollectsMultipleErrorsBeforeThrowing() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                RequiredValuesConfigLoader.decode(emptyMap())
            }

        assertEquals(
            """
            Failed to load config (2 errors):
            - [host] Not found value
            - [port] Not found value
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadResultReturnsValueWhenLoadSucceeds() {
        val result =
            RequiredValuesConfigLoader.loadResultFromString(
                """
                host: localhost
                port: '25565'
                """.trimIndent(),
            )

        assertEquals(KtConfigResult.Success(RequiredValuesConfig("localhost", 25565)), result)
    }

    @Test
    fun testLoadResultReturnsErrorsWhenLoadFails() {
        val result = RequiredValuesConfigLoader.loadResultFromString("")

        assertEquals(
            listOf("host", "port"),
            (result as KtConfigResult.Failure).errors.map(KtConfigError::path),
        )
    }
}
