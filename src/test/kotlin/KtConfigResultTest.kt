import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.KtConfigError
import dev.s7a.ktconfig.KtConfigResult
import dev.s7a.ktconfig.KtConfigValidatable
import dev.s7a.ktconfig.KtConfigValidatorBuilder
import dev.s7a.ktconfig.SerialName
import dev.s7a.ktconfig.exception.KtConfigLoadException
import dev.s7a.ktconfig.format
import dev.s7a.ktconfig.requireIn
import dev.s7a.ktconfig.requireNotBlank
import dev.s7a.ktconfig.requirePositive
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

@KtConfig
data class AutoValidationConfig(
    val host: String,
    val port: Int,
) : KtConfigValidatable<AutoValidationConfig> {
    companion object {
        var validateCount = 0
    }

    override fun KtConfigValidatorBuilder<AutoValidationConfig>.validate() {
        validateCount++
        requireNotBlank(AutoValidationConfig::host)
        requireIn(AutoValidationConfig::port, 1..65535)
    }
}

@KtConfig
data class AutoValidationParentConfig(
    val child: Child,
) {
    @KtConfig
    data class Child(
        val name: String,
    ) : KtConfigValidatable<Child> {
        override fun KtConfigValidatorBuilder<Child>.validate() {
            requireNotBlank(Child::name)
        }
    }
}

@KtConfig
data class DecimalPathParentConfig(
    val parent: Child,
) {
    @KtConfig
    data class Child(
        @SerialName("2.0")
        val value: String,
    )
}

@KtConfig(discriminator = "type")
sealed interface InvalidDiscriminatorConfig {
    @KtConfig
    @SerialName("known")
    data class Known(
        val value: String,
    ) : InvalidDiscriminatorConfig
}

@KtConfig(discriminator = "type")
sealed interface AutoValidationSealedConfig {
    @KtConfig
    @SerialName("child")
    data class Child(
        val count: Int,
    ) : AutoValidationSealedConfig,
        KtConfigValidatable<Child> {
        override fun KtConfigValidatorBuilder<Child>.validate() {
            requirePositive(Child::count)
        }
    }
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

        assertErrorMessage(
            """
            Failed to load config (2 errors):
            - [host] Not found value
            - [port] Not found value
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors,
        )
    }

    @Test
    fun testLoadRunsConfigValidationAutomatically() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                AutoValidationConfigLoader.loadFromString(
                    """
                    host: ''
                    port: 70000
                    """.trimIndent(),
                )
            }

        assertEquals(
            """
            Failed to load config (2 errors):
            - [host] host must not be blank
            - [port] port must be in 1..65535
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testLoadResultReturnsAutomaticValidationErrors() {
        val result =
            AutoValidationConfigLoader.loadResultFromString(
                """
                host: ''
                port: 70000
                """.trimIndent(),
            )

        assertErrorMessage(
            """
            Failed to load config (2 errors):
            - [host] host must not be blank
            - [port] port must be in 1..65535
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors,
        )
    }

    @Test
    fun testDecodeRunsConfigValidationAutomatically() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                AutoValidationConfigLoader.decode(mapOf("host" to "", "port" to 70000))
            }

        assertErrorMessage(
            """
            Failed to load config (2 errors):
            - [host] host must not be blank
            - [port] port must be in 1..65535
            """.trimIndent(),
            exception.errors,
        )
    }

    @Test
    fun testLoadReturnsValueWhenAutomaticValidationPasses() {
        AutoValidationConfig.validateCount = 0

        val result =
            AutoValidationConfigLoader.loadResultFromString(
                """
                host: localhost
                port: 25565
                """.trimIndent(),
            )

        assertEquals(KtConfigResult.Success(AutoValidationConfig("localhost", 25565)), result)
        assertEquals(1, AutoValidationConfig.validateCount)
    }

    @Test
    fun testLoadDoesNotRunAutomaticValidationWhenLoadingFails() {
        AutoValidationConfig.validateCount = 0

        val result = AutoValidationConfigLoader.loadResultFromString("")

        assertErrorMessage(
            """
            Failed to load config (2 errors):
            - [host] Not found value
            - [port] Not found value
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors,
        )
        assertEquals(0, AutoValidationConfig.validateCount)
    }

    @Test
    fun testNestedConfigAutomaticValidationPrefixesParentPath() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                AutoValidationParentConfigLoader.loadFromString(
                    """
                    child:
                      name: ''
                    """.trimIndent(),
                )
            }

        assertErrorMessage(
            """
            Failed to load config (1 error):
            - [child.name] name must not be blank
            """.trimIndent(),
            exception.errors,
        )
    }

    @Test
    fun testNestedConfigDecodeValidationPrefixesParentPath() {
        val exception =
            assertFailsWith<KtConfigLoadException> {
                AutoValidationParentConfigLoader.decode(
                    mapOf(
                        "child" to
                            mapOf(
                                "name" to "",
                            ),
                    ),
                )
            }

        assertErrorMessage(
            """
            Failed to load config (1 error):
            - [child.name] name must not be blank
            """.trimIndent(),
            exception.errors,
        )
    }

    @Test
    fun testNestedLoadingErrorQuotesPathSegmentContainingDot() {
        val result =
            DecimalPathParentConfigLoader.loadResultFromString(
                """
                parent: {}
                """.trimIndent(),
            )

        assertErrorMessage(
            """
            Failed to load config (1 error):
            - [parent.'2.0'] Not found value
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors,
        )
    }

    @Test
    fun testSealedSubtypeAutomaticValidationRunsAfterDispatch() {
        val result =
            AutoValidationSealedConfigLoader.loadResultFromString(
                """
                type: child
                count: 0
                """.trimIndent(),
            )

        assertErrorMessage(
            """
            Failed to load config (1 error):
            - [count] count must be greater than 0
            """.trimIndent(),
            (result as KtConfigResult.Failure).errors,
        )
    }

    private fun assertErrorMessage(
        expected: String,
        errors: List<KtConfigError>,
    ) {
        assertEquals(
            expected,
            errors.format(),
        )
    }
}
