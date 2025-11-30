package dev.s7a.ktconfig.serializer

import java.time.Instant

/**
 * Serializer for [Instant] that transforms between [Instant] and [String] representations.
 * Uses ISO-8601 instant format for string representation.
 *
 * @since 2.0.0
 */
object InstantSerializer : TransformSerializer<Instant, String>(StringSerializer) {
    override fun transform(value: String): Instant = Instant.parse(value)

    override fun transformBack(value: Instant) = value.toString()
}
