package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Type alias for [Location] that uses [FormatedLocationSerializer] for string-based serialization.
 *
 * Represents a location in a world with format: "World, X, Y, Z(, Yaw, Pitch)"
 * where Yaw and Pitch are optional components.
 *
 * @since 2.0.0
 */
typealias FormattedLocation =
    @UseSerializer(FormatedLocationSerializer::class)
    Location

/**
 * Serializer for converting [Location] objects to and from string format.
 *
 * String format: "World, X, Y, Z" or "World, X, Y, Z, Yaw, Pitch"
 * - World: The name of the world
 * - X, Y, Z: Double values representing coordinates
 * - Yaw, Pitch: Optional float values for rotation (defaults to 0 if omitted)
 * - Commas separate coordinates
 * - Whitespace around values is trimmed
 *
 * @throws InvalidFormatException if the string format is invalid
 * @since 2.0.0
 */
object FormatedLocationSerializer : TransformSerializer.Keyable<Location, String>(StringSerializer) {
    override fun transform(value: String): Location {
        val split = value.split("\\s*,\\s*".toRegex(), limit = 6)
        val length = split.size
        if (length != 4 && length != 6) {
            throw InvalidFormatException(value, "World, X, Y, Z or World, X, Y, Z, Yaw, Pitch")
        }
        return Location(
            Bukkit.getWorld(split[0].trim()),
            split[1].toDouble(),
            split[2].toDouble(),
            split[3].toDouble(),
            split.getOrNull(4)?.toFloat() ?: 0F,
            split.getOrNull(5)?.toFloat() ?: 0F,
        )
    }

    override fun transformBack(value: Location): String =
        buildString {
            append("${value.world?.name}, ${value.x}, ${value.y}, ${value.z}")
            if (value.yaw != 0F || value.pitch != 0F) {
                append(", ${value.yaw}, ${value.pitch}")
            }
        }
}
