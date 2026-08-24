package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import net.minecraft.world.phys.Vec3

/**
 * Serializer for converting [Vec3] values to and from `X, Y, Z` strings.
 *
 * Whitespace around each comma-separated decimal coordinate is ignored.
 *
 * @throws InvalidFormatException if the value does not contain exactly three coordinates
 * @throws NumberFormatException if a coordinate is not a valid number
 * @since 2.3.0
 */
object Vec3Serializer : TransformSerializer<Vec3, String>(StringSerializer) {
    override fun decode(value: String): Vec3 {
        val values = value.coordinates(3)
        return Vec3(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
    }

    override fun encode(value: Vec3) = "${value.x}, ${value.y}, ${value.z}"
}
