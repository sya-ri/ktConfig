package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import net.minecraft.core.BlockPos

/**
 * Serializer for converting [BlockPos] values to and from `X, Y, Z` strings.
 *
 * Whitespace around each comma-separated integer coordinate is ignored.
 *
 * @throws InvalidFormatException if the value does not contain exactly three coordinates
 * @throws NumberFormatException if a coordinate is not a valid integer
 * @since 2.3.0
 */
object BlockPosSerializer : TransformSerializer<BlockPos, String>(StringSerializer) {
    override fun decode(value: String): BlockPos {
        val values = value.coordinates(3)
        return BlockPos(values[0].toInt(), values[1].toInt(), values[2].toInt())
    }

    override fun encode(value: BlockPos) = "${value.x}, ${value.y}, ${value.z}"
}
