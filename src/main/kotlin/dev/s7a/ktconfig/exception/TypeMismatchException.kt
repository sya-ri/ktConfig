package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

/**
 * Expected type and actual type are different.
 *
 * @since 1.0.0
 */
class TypeMismatchException(type: KType, value: Any?, path: String) : IllegalArgumentException("Expected $type, but ${if (value != null) "${value::class.qualifiedName}($value)" else "null"}: $path")
