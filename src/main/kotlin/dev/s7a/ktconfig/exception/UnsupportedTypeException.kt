package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

/**
 * Unsupported type was used.
 *
 * @since 1.0.0
 */
class UnsupportedTypeException(type: KType, forName: String, path: String) : IllegalArgumentException("$type is unsupported for $forName ($path)")
