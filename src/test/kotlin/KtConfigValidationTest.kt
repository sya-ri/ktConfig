import dev.s7a.ktconfig.format
import dev.s7a.ktconfig.requireAll
import dev.s7a.ktconfig.requireAtLeast
import dev.s7a.ktconfig.requireContainsKey
import dev.s7a.ktconfig.requireGreaterThanOrEqual
import dev.s7a.ktconfig.requireIn
import dev.s7a.ktconfig.requireLessThanOrEqual
import dev.s7a.ktconfig.requireMinSize
import dev.s7a.ktconfig.requireNotBlank
import dev.s7a.ktconfig.requireNotEmpty
import dev.s7a.ktconfig.requireNullOrNotBlank
import dev.s7a.ktconfig.requirePositive
import dev.s7a.ktconfig.requireSize
import dev.s7a.ktconfig.requireUnique
import dev.s7a.ktconfig.validate
import kotlin.test.Test
import kotlin.test.assertEquals

data class ValidationServerConfig(
    val host: String,
    val port: Int,
    val maxPlayers: Int,
)

data class ValidationNullableStringConfig(
    val name: String?,
)

data class ValidationCollectionConfig(
    val names: List<String>,
    val aliases: Array<String>,
    val ports: IntArray,
    val metadata: Map<String, String>,
)

data class ValidationRangeConfig(
    val min: Int,
    val max: Int,
)

class KtConfigValidationTest {
    @Test
    fun testValidatorReturnsNoErrorsWhenAllRulesPass() {
        val validator =
            validate<ValidationServerConfig> {
                requireNotBlank(ValidationServerConfig::host)
                requireIn(ValidationServerConfig::port, 1..65535)
                requireAtLeast(ValidationServerConfig::maxPlayers, 1)
            }

        assertEquals(emptyList(), validator.validate(ValidationServerConfig("localhost", 25565, 20)))
    }

    @Test
    fun testValidatorCollectsPropertyErrors() {
        val validator =
            validate<ValidationServerConfig> {
                requireNotBlank(ValidationServerConfig::host)
                requireIn(ValidationServerConfig::port, 1..65535)
                requirePositive(ValidationServerConfig::maxPlayers)
            }

        val errors = validator.validate(ValidationServerConfig("", 70000, 0))

        assertEquals(
            """
            Failed to load config (3 errors):
            - [host] host must not be blank
            - [port] port must be in 1..65535
            - [maxPlayers] maxPlayers must be greater than 0
            """.trimIndent(),
            errors.format(),
        )
    }

    @Test
    fun testNullableStringRuleAllowsNullAndRejectsBlank() {
        val validator =
            validate<ValidationNullableStringConfig> {
                requireNullOrNotBlank(ValidationNullableStringConfig::name)
            }

        assertEquals(emptyList(), validator.validate(ValidationNullableStringConfig(null)))
        assertEquals(
            """
            Failed to load config (1 error):
            - [name] name must be null or not blank
            """.trimIndent(),
            validator.validate(ValidationNullableStringConfig("")).format(),
        )
    }

    @Test
    fun testCollectionMapAndArrayRules() {
        val validator =
            validate<ValidationCollectionConfig> {
                requireNotEmpty(ValidationCollectionConfig::names)
                requireUnique(ValidationCollectionConfig::names)
                requireAll(ValidationCollectionConfig::names, "names must not be blank") { it.isNotBlank() }
                requireMinSize(ValidationCollectionConfig::aliases, 2)
                requireSize(ValidationCollectionConfig::ports, 2)
                requireContainsKey(ValidationCollectionConfig::metadata, "required")
            }

        val errors =
            validator.validate(
                ValidationCollectionConfig(
                    names = listOf("a", "a", ""),
                    aliases = arrayOf("main"),
                    ports = intArrayOf(25565),
                    metadata = emptyMap(),
                ),
            )

        assertEquals(
            """
            Failed to load config (5 errors):
            - [names] names must contain unique values
            - [names] names must not be blank
            - [aliases] aliases size must be at least 2
            - [ports] ports size must be 2
            - [metadata] metadata must contain key required
            """.trimIndent(),
            errors.format(),
        )
    }

    @Test
    fun testObjectLevelAndFieldToFieldRulesUseRootPath() {
        val validator =
            validate<ValidationRangeConfig> {
                require("min must be less than or equal to max") {
                    it.min <= it.max
                }
                requireLessThanOrEqual(ValidationRangeConfig::min, ValidationRangeConfig::max)
                requireGreaterThanOrEqual(ValidationRangeConfig::max, ValidationRangeConfig::min)
            }

        val errors = validator.validate(ValidationRangeConfig(10, 1))

        assertEquals(
            """
            Failed to load config (3 errors):
            - min must be less than or equal to max
            - min must be less than or equal to max
            - max must be greater than or equal to min
            """.trimIndent(),
            errors.format(),
        )
    }

    @Test
    fun testAnyOfPassesWhenOneNestedRulePasses() {
        val validator =
            validate<ValidationServerConfig> {
                anyOf("host or positive port is required") {
                    requireNotBlank(ValidationServerConfig::host)
                    requirePositive(ValidationServerConfig::port)
                }
            }

        assertEquals(emptyList(), validator.validate(ValidationServerConfig("", 25565, 20)))
    }

    @Test
    fun testAnyOfReturnsSingleRootErrorWhenAllNestedRulesFail() {
        val validator =
            validate<ValidationServerConfig> {
                anyOf("host or positive port is required") {
                    requireNotBlank(ValidationServerConfig::host)
                    requirePositive(ValidationServerConfig::port)
                }
            }

        val errors = validator.validate(ValidationServerConfig("", 0, 20))

        assertEquals(
            """
            Failed to load config (1 error):
            - host or positive port is required
            """.trimIndent(),
            errors.format(),
        )
    }
}
