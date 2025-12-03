package dev.s7a.ktconfig.serializer

import java.time.OffsetDateTime

/**
 * Serializer for [OffsetDateTime] that transforms between [OffsetDateTime] and [String] representations.
 * Uses ISO-8601 date-time format for string representation.
 *
 * @since 2.0.0
 */
object OffsetDateTimeSerializer : TransformSerializer<OffsetDateTime, String>(StringSerializer) {
    override fun decode(value: String): OffsetDateTime = OffsetDateTime.parse(value)

    override fun encode(value: OffsetDateTime) = value.toString()
}
