package dev.s7a.ktconfig.serializer

import java.time.Period

/**
 * Serializer for [Period] that transforms between [Period] and [String] representations.
 * Uses ISO-8601 year format for string representation.
 *
 * @since 2.0.0
 */
object PeriodSerializer : TransformSerializer<Period, String>(StringSerializer) {
    override fun transform(value: String): Period = Period.parse(value)

    override fun transformBack(value: Period) = value.toString()
}
