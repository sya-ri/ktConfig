package dev.s7a.ktconfig.serializer

import java.time.LocalDate

/**
 * Serializer for [LocalDate] that transforms between [LocalDate] and [String] representations.
 * Uses ISO-8601 date format for string representation.
 *
 * @since 2.0.0
 */
object LocalDateSerializer : TransformSerializer<LocalDate, String>(StringSerializer) {
    override fun decode(value: String): LocalDate = LocalDate.parse(value)

    override fun encode(value: LocalDate) = value.toString()
}
