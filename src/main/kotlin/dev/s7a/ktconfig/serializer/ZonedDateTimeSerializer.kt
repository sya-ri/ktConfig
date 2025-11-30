package dev.s7a.ktconfig.serializer

import java.time.ZonedDateTime

/**
 * Serializer for [ZonedDateTime] that transforms between [ZonedDateTime] and [String] representations.
 * Uses ISO-8601 date-time format for string representation.
 *
 * @since 2.0.0
 */
object ZonedDateTimeSerializer : TransformSerializer<ZonedDateTime, String>(StringSerializer) {
    override fun transform(value: String): ZonedDateTime = ZonedDateTime.parse(value)

    override fun transformBack(value: ZonedDateTime) = value.toString()
}
