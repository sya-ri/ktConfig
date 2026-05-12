import dev.s7a.ktconfig.KtConfigError
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
import kotlin.test.assertTrue

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

        assertEquals(listOf("host", "port", "maxPlayers"), errors.map(KtConfigError::path))
        assertTrue(errors.all { it.kind == KtConfigError.Kind.InvalidValue })
    }

    @Test
    fun testNullableStringRuleAllowsNullAndRejectsBlank() {
        val validator =
            validate<ValidationNullableStringConfig> {
                requireNullOrNotBlank(ValidationNullableStringConfig::name)
            }

        assertEquals(emptyList(), validator.validate(ValidationNullableStringConfig(null)))
        assertEquals("name", validator.validate(ValidationNullableStringConfig("")).single().path)
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

        assertEquals(listOf("names", "names", "aliases", "ports", "metadata"), errors.map(KtConfigError::path))
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

        assertEquals(listOf("", "", ""), errors.map(KtConfigError::path))
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

        assertEquals(listOf(""), errors.map(KtConfigError::path))
        assertEquals(listOf("host or positive port is required"), errors.map(KtConfigError::message))
    }
}
