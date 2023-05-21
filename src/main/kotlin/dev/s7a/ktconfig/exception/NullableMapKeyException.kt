package dev.s7a.ktconfig.exception

/**
 * Map key must be not-null.
 *
 * @since 1.0.0
 */
class NullableMapKeyException(path: String) : IllegalArgumentException("Map key must be not-null ($path)")
