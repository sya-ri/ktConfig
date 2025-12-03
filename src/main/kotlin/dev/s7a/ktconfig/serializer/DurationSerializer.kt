package dev.s7a.ktconfig.serializer

import java.time.Duration

/**
 * Serializer for [Duration] that transforms between [Duration] and [String] representations.
 * Uses ISO-8601 year format for string representation.
 *
 * @since 2.0.0
 */
object DurationSerializer : TransformSerializer<Duration, String>(StringSerializer) {
    override fun decode(value: String): Duration = Duration.parse(value)

    override fun encode(value: Duration) = value.toString()
}
