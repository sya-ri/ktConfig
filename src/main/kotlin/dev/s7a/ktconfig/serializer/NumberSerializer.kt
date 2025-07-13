package dev.s7a.ktconfig.serializer

import java.math.BigDecimal

object NumberSerializer : ValueSerializer<Number> {
    override fun from(value: Any) =
        when (value) {
            is Number -> value
            is String -> BigDecimal(value)
            else -> throw IllegalArgumentException("Cannot convert to Number: ${value::class.simpleName}")
        }

    override fun to(value: Number) = value.toString()
}
