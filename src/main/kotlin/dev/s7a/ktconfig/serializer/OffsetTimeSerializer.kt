package dev.s7a.ktconfig.serializer

import java.time.OffsetTime

/**
 * Serializer for [OffsetTime] that transforms between [OffsetTime] and [String] representations.
 * Uses ISO-8601 date-time format for string representation.
 *
 * @since 2.0.0
 */
object OffsetTimeSerializer : TransformSerializer<OffsetTime, String>(StringSerializer) {
    override fun transform(value: String): OffsetTime = OffsetTime.parse(value)

    override fun transformBack(value: OffsetTime) = value.toString()
}
