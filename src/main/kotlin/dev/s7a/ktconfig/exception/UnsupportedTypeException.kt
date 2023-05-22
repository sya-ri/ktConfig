package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

/**
 * Unsupported type was used.
 *
 * @since 1.0.0
 */
class UnsupportedTypeException(type: KType, reason: String) : IllegalArgumentException("$type is unsupported type ($reason)")
