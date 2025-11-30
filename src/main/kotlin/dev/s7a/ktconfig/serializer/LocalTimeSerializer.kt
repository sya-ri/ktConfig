package dev.s7a.ktconfig.serializer

import java.time.LocalTime

/**
 * Serializer for [LocalTime] that transforms between [LocalTime] and [String] representations.
 * Uses ISO-8601 time format for string representation.
 *
 * @since 2.0.0
 */
object LocalTimeSerializer : TransformSerializer<LocalTime, String>(StringSerializer) {
    override fun transform(value: String): LocalTime = LocalTime.parse(value)

    override fun transformBack(value: LocalTime) = value.toString()
}
