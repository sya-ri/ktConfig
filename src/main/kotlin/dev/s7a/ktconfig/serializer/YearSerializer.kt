package dev.s7a.ktconfig.serializer

import java.time.Year

/**
 * Serializer for [Year] that transforms between [Year] and [String] representations.
 * Uses ISO-8601 year format for string representation.
 *
 * @since 2.0.0
 */
object YearSerializer : TransformSerializer<Year, String>(StringSerializer) {
    override fun decode(value: String): Year = Year.parse(value)

    override fun encode(value: Year) = value.toString()
}
