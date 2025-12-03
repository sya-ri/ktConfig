package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.util.Vector

/**
 * Type alias for [Vector] that uses [FormattedVectorSerializer] for string-based serialization.
 *
 * Represents a vector with format: "X, Y, Z"
 * where X, Y, Z are double values representing coordinates.
 *
 * @since 2.0.0
 */
typealias FormattedVector =
    @UseSerializer(FormattedVectorSerializer::class)
    Vector

/**
 * Serializer for converting [Vector] objects to and from string format.
 *
 * String format: "X, Y, Z"
 * - X, Y, Z: Double values representing coordinates
 * - Commas separate coordinates
 * - Whitespace around values is trimmed
 *
 * @throws InvalidFormatException if the string format is invalid
 * @since 2.0.0
 */
object FormattedVectorSerializer : TransformSerializer.Keyable<Vector, String>(StringSerializer) {
    override fun decode(value: String): Vector {
        val split = value.split("\\s*,\\s*".toRegex(), limit = 3)
        if (split.size != 3) {
            throw InvalidFormatException(value, "X, Y, Z")
        }
        return Vector(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
    }

    override fun encode(value: Vector) = "${value.x}, ${value.y}, ${value.z}"
}
