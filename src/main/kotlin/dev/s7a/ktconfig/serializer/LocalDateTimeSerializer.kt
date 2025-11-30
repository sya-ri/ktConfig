package dev.s7a.ktconfig.serializer

import java.time.LocalDateTime

/**
 * Serializer for [LocalDateTime] that transforms between [LocalDateTime] and [String] representations.
 * Uses ISO-8601 date-time format for string representation.
 *
 * @since 2.0.0
 */
object LocalDateTimeSerializer : TransformSerializer<LocalDateTime, String>(StringSerializer) {
    override fun transform(value: String): LocalDateTime = LocalDateTime.parse(value)

    override fun transformBack(value: LocalDateTime) = value.toString()
}
