package dev.s7a.ktconfig.serializer

import java.math.BigDecimal

/**
 * Serializer for handling Number type values.
 * Provides functionality to convert between Number objects and their string representations.
 *
 * Note: The kotlin.Number type is not supported as a direct serialization target because it lacks type safety
 * and could lead to precision loss when converting between different numeric types (e.g., Long to Int,
 * Double to Float). Instead, use specific numeric types like Int, Long, Double, etc.
 */
object NumberSerializer : Serializer<Number> {
    override fun deserialize(value: Any) =
        when (value) {
            is Number -> value
            is String -> BigDecimal(value)
            else -> throw IllegalArgumentException("Cannot convert to Number: ${value::class.simpleName}")
        }

    override fun serialize(value: Number) = value.toString()
}
