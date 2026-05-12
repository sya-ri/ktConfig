package dev.s7a.ktconfig

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KProperty1

/**
 * Creates a validator from a validation DSL block.
 *
 * @param T The type of object to validate.
 * @param block The validation rules to register.
 * @return A validator that evaluates all registered rules and returns all failures.
 * @since 2.2.0
 */
fun <T> validate(block: KtConfigValidatorBuilder<T>.() -> Unit): KtConfigValidator<T> =
    KtConfigValidatorBuilder<T>()
        .apply(block)
        .build()

/**
 * Validates a config object and returns structured errors.
 *
 * @param T The type of object to validate.
 * @since 2.2.0
 */
fun interface KtConfigValidator<T> {
    /**
     * Validates [value] and returns all validation errors.
     *
     * @param value The value to validate.
     * @return All validation errors. An empty list means validation passed.
     * @since 2.2.0
     */
    fun validate(value: T): List<KtConfigError>
}

/**
 * Builder used by [validate] to register property-level and object-level validation rules.
 *
 * @param T The type of object being validated.
 * @since 2.2.0
 */
class KtConfigValidatorBuilder<T> {
    private val rules = mutableListOf<(T) -> KtConfigError?>()

    /**
     * Adds a property-level validation rule.
     *
     * @param property The property to validate.
     * @param message The message to report when [predicate] returns false.
     * @param predicate The rule applied to the property value.
     * @since 2.2.0
     */
    fun <V> require(
        property: KProperty1<T, V>,
        message: String,
        predicate: (V) -> Boolean,
    ) {
        rules += { value ->
            val propertyValue = property.get(value)
            if (predicate(propertyValue)) {
                null
            } else {
                KtConfigError(property.name, message)
            }
        }
    }

    /**
     * Adds an object-level validation rule.
     *
     * Object-level errors use an empty path.
     *
     * @param message The message to report when [predicate] returns false.
     * @param predicate The rule applied to the object being validated.
     * @since 2.2.0
     */
    fun require(
        message: String,
        predicate: (T) -> Boolean,
    ) {
        rules += { value ->
            if (predicate(value)) {
                null
            } else {
                KtConfigError("", message)
            }
        }
    }

    /**
     * Adds a grouped validation rule that succeeds when at least one nested rule succeeds.
     *
     * Nested rule errors are not returned directly. If every nested rule fails, this group reports
     * a single object-level error with [message].
     *
     * @param message The message to report when every nested rule fails.
     * @param block The nested rules to evaluate with OR semantics.
     * @since 2.2.0
     */
    fun anyOf(
        message: String,
        block: KtConfigValidatorBuilder<T>.() -> Unit,
    ) {
        val nestedRules =
            KtConfigValidatorBuilder<T>()
                .apply(block)
                .rules
                .toList()
        rules += { value ->
            if (nestedRules.any { rule -> rule(value) == null }) {
                null
            } else {
                KtConfigError("", message)
            }
        }
    }

    /**
     * Builds a validator from the rules registered in this builder.
     *
     * @return A validator that evaluates all registered rules.
     * @since 2.2.0
     */
    fun build(): KtConfigValidator<T> =
        KtConfigValidator { value ->
            rules.mapNotNull { rule -> rule(value) }
        }
}

/**
 * Adds a rule that requires the property value to be non-blank.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireNotBlank(property: KProperty1<T, String>) =
    require(property, "${property.name} must not be blank", String::isNotBlank)

/**
 * Adds a rule that requires the nullable property value to be null or non-blank.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrNotBlank")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrNotBlank(property: KProperty1<T, String?>) =
    require(property, "${property.name} must be null or not blank") { it == null || it.isNotBlank() }

/**
 * Adds a rule that requires the property value to be non-empty.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireNotEmpty(property: KProperty1<T, String>) =
    require(property, "${property.name} must not be empty", String::isNotEmpty)

/**
 * Adds a rule that requires the nullable property value to be null or non-empty.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrNotEmpty")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrNotEmpty(property: KProperty1<T, String?>) =
    require(property, "${property.name} must be null or not empty") { it == null || it.isNotEmpty() }

/**
 * Adds a rule that requires the string length to equal the expected length.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireLength(
    property: KProperty1<T, String>,
    length: Int,
) = require(property, "${property.name} length must be $length") { it.length == length }

/**
 * Adds a rule that requires the nullable string to be null or have the expected length.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrLength")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrLength(
    property: KProperty1<T, String?>,
    length: Int,
) = require(property, "${property.name} must be null or have length $length") { it == null || it.length == length }

/**
 * Adds a rule that requires the string length to be inside the expected range.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireLengthIn(
    property: KProperty1<T, String>,
    range: IntRange,
) = require(property, "${property.name} length must be in $range") { it.length in range }

/**
 * Adds a rule that requires the nullable string to be null or have a length inside the expected range.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrLengthIn")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrLengthIn(
    property: KProperty1<T, String?>,
    range: IntRange,
) = require(property, "${property.name} must be null or have length in $range") { it == null || it.length in range }

/**
 * Adds a rule that requires the string length to be at least the expected minimum.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireMinLength(
    property: KProperty1<T, String>,
    min: Int,
) = require(property, "${property.name} length must be at least $min") { it.length >= min }

/**
 * Adds a rule that requires the nullable string to be null or have at least the expected minimum length.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrMinLength")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrMinLength(
    property: KProperty1<T, String?>,
    min: Int,
) = require(property, "${property.name} must be null or have length at least $min") { it == null || it.length >= min }

/**
 * Adds a rule that requires the string length to be at most the expected maximum.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireMaxLength(
    property: KProperty1<T, String>,
    max: Int,
) = require(property, "${property.name} length must be at most $max") { it.length <= max }

/**
 * Adds a rule that requires the nullable string to be null or have at most the expected maximum length.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrMaxLength")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrMaxLength(
    property: KProperty1<T, String?>,
    max: Int,
) = require(property, "${property.name} must be null or have length at most $max") { it == null || it.length <= max }

/**
 * Adds a rule that requires the string to match the expected regular expression.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireMatches(
    property: KProperty1<T, String>,
    regex: Regex,
) = require(property, "${property.name} must match $regex") { it.matches(regex) }

/**
 * Adds a rule that requires the nullable string to be null or match the expected regular expression.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrMatches")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrMatches(
    property: KProperty1<T, String?>,
    regex: Regex,
) = require(property, "${property.name} must be null or match $regex") { it == null || it.matches(regex) }

/**
 * Adds a rule that requires the string to start with the expected prefix.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireStartsWith(
    property: KProperty1<T, String>,
    prefix: String,
) = require(property, "${property.name} must start with $prefix") { it.startsWith(prefix) }

/**
 * Adds a rule that requires the nullable string to be null or start with the expected prefix.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrStartsWith")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrStartsWith(
    property: KProperty1<T, String?>,
    prefix: String,
) = require(property, "${property.name} must be null or start with $prefix") { it == null || it.startsWith(prefix) }

/**
 * Adds a rule that requires the string to end with the expected suffix.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireEndsWith(
    property: KProperty1<T, String>,
    suffix: String,
) = require(property, "${property.name} must end with $suffix") { it.endsWith(suffix) }

/**
 * Adds a rule that requires the nullable string to be null or end with the expected suffix.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrEndsWith")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrEndsWith(
    property: KProperty1<T, String?>,
    suffix: String,
) = require(property, "${property.name} must be null or end with $suffix") { it == null || it.endsWith(suffix) }

/**
 * Adds a rule that requires the value to contain the expected element or text.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireContains(
    property: KProperty1<T, String>,
    text: String,
) = require(property, "${property.name} must contain $text") { it.contains(text) }

/**
 * Adds a rule that requires the nullable string to be null or contain the expected text.
 *
 * @since 2.2.0
 */
@JvmName("requireNullableStringNullOrContains")
fun <T> KtConfigValidatorBuilder<T>.requireNullOrContains(
    property: KProperty1<T, String?>,
    text: String,
) = require(property, "${property.name} must be null or contain $text") { it == null || it.contains(text) }

/**
 * Adds a rule that requires the comparable value to be inside the expected range.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireIn(
    property: KProperty1<T, V>,
    range: ClosedRange<V>,
) = require(property, "${property.name} must be in $range") { it in range }

/**
 * Adds a rule that requires the comparable value to be at least the expected minimum.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireAtLeast(
    property: KProperty1<T, V>,
    min: V,
) = require(property, "${property.name} must be at least $min") { it >= min }

/**
 * Adds a rule that requires the comparable value to be at most the expected maximum.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireAtMost(
    property: KProperty1<T, V>,
    max: V,
) = require(property, "${property.name} must be at most $max") { it <= max }

/**
 * Adds a rule that requires the value to be greater than another value.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireGreaterThan(
    property: KProperty1<T, V>,
    min: V,
) = require(property, "${property.name} must be greater than $min") { it > min }

/**
 * Adds a rule that requires the value to be less than another value.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireLessThan(
    property: KProperty1<T, V>,
    max: V,
) = require(property, "${property.name} must be less than $max") { it < max }

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBytePositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Byte>) = requireGreaterThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireShortPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Short>) = requireGreaterThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireIntPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Int>) = requireGreaterThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireLongPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Long>) = requireGreaterThan(property, 0L)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireFloatPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Float>) = requireGreaterThan(property, 0f)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireDoublePositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, Double>) = requireGreaterThan(property, 0.0)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigIntegerPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, BigInteger>) = requireGreaterThan(property, BigInteger.ZERO)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigDecimalPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, BigDecimal>) = requireGreaterThan(property, BigDecimal.ZERO)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUBytePositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, UByte>) = requireGreaterThan(property, 0.toUByte())

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUShortPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, UShort>) = requireGreaterThan(property, 0.toUShort())

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUIntPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, UInt>) = requireGreaterThan(property, 0u)

/**
 * Adds a rule that requires the numeric value to be greater than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireULongPositive")
fun <T> KtConfigValidatorBuilder<T>.requirePositive(property: KProperty1<T, ULong>) = requireGreaterThan(property, 0uL)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBytePositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Byte>) = requireAtLeast(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireShortPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Short>) = requireAtLeast(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireIntPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Int>) = requireAtLeast(property, 0)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireLongPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Long>) = requireAtLeast(property, 0L)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireFloatPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Float>) = requireAtLeast(property, 0f)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireDoublePositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, Double>) = requireAtLeast(property, 0.0)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigIntegerPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, BigInteger>) = requireAtLeast(property, BigInteger.ZERO)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigDecimalPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, BigDecimal>) = requireAtLeast(property, BigDecimal.ZERO)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUBytePositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, UByte>) = requireAtLeast(property, 0.toUByte())

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUShortPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, UShort>) = requireAtLeast(property, 0.toUShort())

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireUIntPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, UInt>) = requireAtLeast(property, 0u)

/**
 * Adds a rule that requires the numeric value to be greater than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireULongPositiveOrZero")
fun <T> KtConfigValidatorBuilder<T>.requirePositiveOrZero(property: KProperty1<T, ULong>) = requireAtLeast(property, 0uL)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireByteNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Byte>) = requireLessThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireShortNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Short>) = requireLessThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireIntNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Int>) = requireLessThan(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireLongNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Long>) = requireLessThan(property, 0L)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireFloatNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Float>) = requireLessThan(property, 0f)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireDoubleNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, Double>) = requireLessThan(property, 0.0)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigIntegerNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, BigInteger>) = requireLessThan(property, BigInteger.ZERO)

/**
 * Adds a rule that requires the numeric value to be less than zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigDecimalNegative")
fun <T> KtConfigValidatorBuilder<T>.requireNegative(property: KProperty1<T, BigDecimal>) = requireLessThan(property, BigDecimal.ZERO)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireByteNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Byte>) = requireAtMost(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireShortNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Short>) = requireAtMost(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireIntNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Int>) = requireAtMost(property, 0)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireLongNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Long>) = requireAtMost(property, 0L)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireFloatNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Float>) = requireAtMost(property, 0f)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireDoubleNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, Double>) = requireAtMost(property, 0.0)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigIntegerNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, BigInteger>) = requireAtMost(property, BigInteger.ZERO)

/**
 * Adds a rule that requires the numeric value to be less than or equal to zero.
 *
 * @since 2.2.0
 */
@JvmName("requireBigDecimalNegativeOrZero")
fun <T> KtConfigValidatorBuilder<T>.requireNegativeOrZero(property: KProperty1<T, BigDecimal>) = requireAtMost(property, BigDecimal.ZERO)

/**
 * Adds a rule that requires the boolean value to be true.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireTrue(property: KProperty1<T, Boolean>) =
    require(property, "${property.name} must be true") { it }

/**
 * Adds a rule that requires the boolean value to be false.
 *
 * @since 2.2.0
 */
fun <T> KtConfigValidatorBuilder<T>.requireFalse(property: KProperty1<T, Boolean>) =
    require(property, "${property.name} must be false") { !it }

/**
 * Adds a rule that requires the nullable value to be non-null.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireNotNull(property: KProperty1<T, V?>) =
    require(property, "${property.name} must not be null") { it != null }

/**
 * Adds a rule that requires the nullable value to be null.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireNull(property: KProperty1<T, V?>) =
    require(property, "${property.name} must be null") { it == null }

/**
 * Adds a rule that requires the nullable value to be null or satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireNullOr(
    property: KProperty1<T, V?>,
    message: String,
    predicate: (V) -> Boolean,
) = require(property, message) { it == null || predicate(it) }

/**
 * Adds a rule that requires the property value to be non-empty.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionNotEmpty")
fun <T, E> KtConfigValidatorBuilder<T>.requireNotEmpty(property: KProperty1<T, Collection<E>>) =
    require(property, "${property.name} must not be empty") { it.isNotEmpty() }

/**
 * Adds a rule that requires the collection, map, or array to be empty.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionEmpty")
fun <T, E> KtConfigValidatorBuilder<T>.requireEmpty(property: KProperty1<T, Collection<E>>) =
    require(property, "${property.name} must be empty") { it.isEmpty() }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionSize")
fun <T, E> KtConfigValidatorBuilder<T>.requireSize(
    property: KProperty1<T, Collection<E>>,
    size: Int,
) = require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to be inside the expected range.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionSizeIn")
fun <T, E> KtConfigValidatorBuilder<T>.requireSizeIn(
    property: KProperty1<T, Collection<E>>,
    range: IntRange,
) = require(property, "${property.name} size must be in $range") { it.size in range }

/**
 * Adds a rule that requires the collection, map, or array size to be at least the expected minimum.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionMinSize")
fun <T, E> KtConfigValidatorBuilder<T>.requireMinSize(
    property: KProperty1<T, Collection<E>>,
    min: Int,
) = require(property, "${property.name} size must be at least $min") { it.size >= min }

/**
 * Adds a rule that requires the collection, map, or array size to be at most the expected maximum.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionMaxSize")
fun <T, E> KtConfigValidatorBuilder<T>.requireMaxSize(
    property: KProperty1<T, Collection<E>>,
    max: Int,
) = require(property, "${property.name} size must be at most $max") { it.size <= max }

/**
 * Adds a rule that requires the value to contain the expected element or text.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionContains")
fun <T, E> KtConfigValidatorBuilder<T>.requireContains(
    property: KProperty1<T, Collection<E>>,
    element: E,
) = require(property, "${property.name} must contain $element") { element in it }

/**
 * Adds a rule that requires the collection or array not to contain the expected element.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionNotContains")
fun <T, E> KtConfigValidatorBuilder<T>.requireNotContains(
    property: KProperty1<T, Collection<E>>,
    element: E,
) = require(property, "${property.name} must not contain $element") { element !in it }

/**
 * Adds a rule that requires all elements to satisfy the predicate.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionAll")
fun <T, E> KtConfigValidatorBuilder<T>.requireAll(
    property: KProperty1<T, Collection<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.all(predicate) }

/**
 * Adds a rule that requires at least one element to satisfy the predicate.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionAny")
fun <T, E> KtConfigValidatorBuilder<T>.requireAny(
    property: KProperty1<T, Collection<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.any(predicate) }

/**
 * Adds a rule that requires no elements to satisfy the predicate.
 *
 * @since 2.2.0
 */
@JvmName("requireCollectionNone")
fun <T, E> KtConfigValidatorBuilder<T>.requireNone(
    property: KProperty1<T, Collection<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.none(predicate) }

/**
 * Adds a rule that requires all elements to be unique.
 *
 * @since 2.2.0
 */
fun <T, E> KtConfigValidatorBuilder<T>.requireUnique(property: KProperty1<T, Collection<E>>) =
    require(property, "${property.name} must contain unique values") { it.toSet().size == it.size }

/**
 * Adds a rule that requires all selected element keys to be unique.
 *
 * @since 2.2.0
 */
fun <T, E, K> KtConfigValidatorBuilder<T>.requireUniqueBy(
    property: KProperty1<T, Collection<E>>,
    selector: (E) -> K,
) = require(property, "${property.name} must contain unique values") { collection ->
    collection.map(selector).toSet().size == collection.size
}

/**
 * Adds a rule that requires the property value to be non-empty.
 *
 * @since 2.2.0
 */
@JvmName("requireMapNotEmpty")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireNotEmpty(property: KProperty1<T, Map<K, V>>) =
    require(property, "${property.name} must not be empty") { it.isNotEmpty() }

/**
 * Adds a rule that requires the collection, map, or array to be empty.
 *
 * @since 2.2.0
 */
@JvmName("requireMapEmpty")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireEmpty(property: KProperty1<T, Map<K, V>>) =
    require(property, "${property.name} must be empty") { it.isEmpty() }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireMapSize")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireSize(
    property: KProperty1<T, Map<K, V>>,
    size: Int,
) = require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to be inside the expected range.
 *
 * @since 2.2.0
 */
@JvmName("requireMapSizeIn")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireSizeIn(
    property: KProperty1<T, Map<K, V>>,
    range: IntRange,
) = require(property, "${property.name} size must be in $range") { it.size in range }

/**
 * Adds a rule that requires the collection, map, or array size to be at least the expected minimum.
 *
 * @since 2.2.0
 */
@JvmName("requireMapMinSize")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireMinSize(
    property: KProperty1<T, Map<K, V>>,
    min: Int,
) = require(property, "${property.name} size must be at least $min") { it.size >= min }

/**
 * Adds a rule that requires the collection, map, or array size to be at most the expected maximum.
 *
 * @since 2.2.0
 */
@JvmName("requireMapMaxSize")
fun <T, K, V> KtConfigValidatorBuilder<T>.requireMaxSize(
    property: KProperty1<T, Map<K, V>>,
    max: Int,
) = require(property, "${property.name} size must be at most $max") { it.size <= max }

/**
 * Adds a rule that requires the map to contain the expected key.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireContainsKey(
    property: KProperty1<T, Map<K, V>>,
    key: K,
) = require(property, "${property.name} must contain key $key") { it.containsKey(key) }

/**
 * Adds a rule that requires the map not to contain the expected key.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireNotContainsKey(
    property: KProperty1<T, Map<K, V>>,
    key: K,
) = require(property, "${property.name} must not contain key $key") { !it.containsKey(key) }

/**
 * Adds a rule that requires the map to contain the expected value.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireContainsValue(
    property: KProperty1<T, Map<K, V>>,
    value: V,
) = require(property, "${property.name} must contain value $value") { it.containsValue(value) }

/**
 * Adds a rule that requires the map not to contain the expected value.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireNotContainsValue(
    property: KProperty1<T, Map<K, V>>,
    value: V,
) = require(property, "${property.name} must not contain value $value") { !it.containsValue(value) }

/**
 * Adds a rule that requires all map keys to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireAllKeys(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (K) -> Boolean,
) = require(property, message) { it.keys.all(predicate) }

/**
 * Adds a rule that requires at least one map key to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireAnyKey(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (K) -> Boolean,
) = require(property, message) { it.keys.any(predicate) }

/**
 * Adds a rule that requires no map keys to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireNoKeys(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (K) -> Boolean,
) = require(property, message) { it.keys.none(predicate) }

/**
 * Adds a rule that requires all map values to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireAllValues(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (V) -> Boolean,
) = require(property, message) { it.values.all(predicate) }

/**
 * Adds a rule that requires at least one map value to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireAnyValue(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (V) -> Boolean,
) = require(property, message) { it.values.any(predicate) }

/**
 * Adds a rule that requires no map values to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, K, V> KtConfigValidatorBuilder<T>.requireNoValues(
    property: KProperty1<T, Map<K, V>>,
    message: String,
    predicate: (V) -> Boolean,
) = require(property, message) { it.values.none(predicate) }

/**
 * Adds a rule that requires the property value to be non-empty.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayNotEmpty")
fun <T, E> KtConfigValidatorBuilder<T>.requireNotEmpty(property: KProperty1<T, Array<E>>) =
    require(property, "${property.name} must not be empty") { it.isNotEmpty() }

/**
 * Adds a rule that requires the collection, map, or array to be empty.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayEmpty")
fun <T, E> KtConfigValidatorBuilder<T>.requireEmpty(property: KProperty1<T, Array<E>>) =
    require(property, "${property.name} must be empty") { it.isEmpty() }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireArraySize")
fun <T, E> KtConfigValidatorBuilder<T>.requireSize(
    property: KProperty1<T, Array<E>>,
    size: Int,
) = require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to be inside the expected range.
 *
 * @since 2.2.0
 */
@JvmName("requireArraySizeIn")
fun <T, E> KtConfigValidatorBuilder<T>.requireSizeIn(
    property: KProperty1<T, Array<E>>,
    range: IntRange,
) = require(property, "${property.name} size must be in $range") { it.size in range }

/**
 * Adds a rule that requires the collection, map, or array size to be at least the expected minimum.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayMinSize")
fun <T, E> KtConfigValidatorBuilder<T>.requireMinSize(
    property: KProperty1<T, Array<E>>,
    min: Int,
) = require(property, "${property.name} size must be at least $min") { it.size >= min }

/**
 * Adds a rule that requires the collection, map, or array size to be at most the expected maximum.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayMaxSize")
fun <T, E> KtConfigValidatorBuilder<T>.requireMaxSize(
    property: KProperty1<T, Array<E>>,
    max: Int,
) = require(property, "${property.name} size must be at most $max") { it.size <= max }

/**
 * Adds a rule that requires the value to contain the expected element or text.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayContains")
fun <T, E> KtConfigValidatorBuilder<T>.requireContains(
    property: KProperty1<T, Array<E>>,
    element: E,
) = require(property, "${property.name} must contain $element") { element in it }

/**
 * Adds a rule that requires the collection or array not to contain the expected element.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayNotContains")
fun <T, E> KtConfigValidatorBuilder<T>.requireNotContains(
    property: KProperty1<T, Array<E>>,
    element: E,
) = require(property, "${property.name} must not contain $element") { element !in it }

/**
 * Adds a rule that requires all elements to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, E> KtConfigValidatorBuilder<T>.requireAll(
    property: KProperty1<T, Array<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.all(predicate) }

/**
 * Adds a rule that requires at least one element to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, E> KtConfigValidatorBuilder<T>.requireAny(
    property: KProperty1<T, Array<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.any(predicate) }

/**
 * Adds a rule that requires no elements to satisfy the predicate.
 *
 * @since 2.2.0
 */
fun <T, E> KtConfigValidatorBuilder<T>.requireNone(
    property: KProperty1<T, Array<E>>,
    message: String,
    predicate: (E) -> Boolean,
) = require(property, message) { it.none(predicate) }

/**
 * Adds a rule that requires all elements to be unique.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayUnique")
fun <T, E> KtConfigValidatorBuilder<T>.requireUnique(property: KProperty1<T, Array<E>>) =
    require(property, "${property.name} must contain unique values") { it.toSet().size == it.size }

/**
 * Adds a rule that requires all selected element keys to be unique.
 *
 * @since 2.2.0
 */
@JvmName("requireArrayUniqueBy")
fun <T, E, K> KtConfigValidatorBuilder<T>.requireUniqueBy(
    property: KProperty1<T, Array<E>>,
    selector: (E) -> K,
) = require(property, "${property.name} must contain unique values") { array ->
    array.map(selector).toSet().size == array.size
}

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireByteArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(
    property: KProperty1<T, ByteArray>,
    size: Int,
) = require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireShortArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, ShortArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireIntArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, IntArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireLongArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, LongArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireFloatArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, FloatArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireDoubleArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, DoubleArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireBooleanArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, BooleanArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the collection, map, or array size to equal the expected size.
 *
 * @since 2.2.0
 */
@JvmName("requireCharArraySize")
fun <T> KtConfigValidatorBuilder<T>.requireSize(property: KProperty1<T, CharArray>, size: Int) =
    require(property, "${property.name} size must be $size") { it.size == size }

/**
 * Adds a rule that requires the value to be one of the allowed values.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireOneOf(
    property: KProperty1<T, V>,
    allowedValues: Iterable<V>,
) = require(property, "${property.name} must be one of ${allowedValues.joinToString()}") { it in allowedValues }

/**
 * Adds a rule that requires the value not to be one of the disallowed values.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireNotOneOf(
    property: KProperty1<T, V>,
    disallowedValues: Iterable<V>,
) = require(property, "${property.name} must not be one of ${disallowedValues.joinToString()}") { it !in disallowedValues }

/**
 * Adds a field-to-field rule that requires both property values to be equal.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireEqual(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must equal ${rightProperty.name}") { leftProperty.get(it) == rightProperty.get(it) }

/**
 * Adds a field-to-field rule that requires both property values to be different.
 *
 * @since 2.2.0
 */
fun <T, V> KtConfigValidatorBuilder<T>.requireNotEqual(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must not equal ${rightProperty.name}") { leftProperty.get(it) != rightProperty.get(it) }

/**
 * Adds a rule that requires the value to be less than another value.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireLessThan(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must be less than ${rightProperty.name}") { leftProperty.get(it) < rightProperty.get(it) }

/**
 * Adds a field-to-field rule that requires the left property to be less than or equal to the right property.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireLessThanOrEqual(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must be less than or equal to ${rightProperty.name}") {
    leftProperty.get(it) <= rightProperty.get(it)
}

/**
 * Adds a rule that requires the value to be greater than another value.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireGreaterThan(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must be greater than ${rightProperty.name}") { leftProperty.get(it) > rightProperty.get(it) }

/**
 * Adds a field-to-field rule that requires the left property to be greater than or equal to the right property.
 *
 * @since 2.2.0
 */
fun <T, V : Comparable<V>> KtConfigValidatorBuilder<T>.requireGreaterThanOrEqual(
    leftProperty: KProperty1<T, V>,
    rightProperty: KProperty1<T, V>,
) = require("${leftProperty.name} must be greater than or equal to ${rightProperty.name}") {
    leftProperty.get(it) >= rightProperty.get(it)
}
