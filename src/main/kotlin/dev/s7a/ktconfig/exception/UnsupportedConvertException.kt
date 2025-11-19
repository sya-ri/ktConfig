package dev.s7a.ktconfig.exception

import kotlin.reflect.KClass

/**
 * Exception thrown when a value cannot be converted to the expected type.
 *
 * @property actual The actual class type of the value that failed to convert
 * @property expected The target class type that the conversion was attempted to
 * @since 2.0.0
 */
class UnsupportedConvertException(
    val actual: KClass<*>,
    val expected: KClass<*>,
) : KtConfigException("Unsupported convert: ${actual.qualifiedName} -> ${expected.qualifiedName}")
