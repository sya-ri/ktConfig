package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.util.BlockVector

/**
 * Type alias for [BlockVector] that uses [FormattedBlockVectorSerializer] for string-based serialization.
 *
 * Represents a block vector with format: "X, Y, Z" where X, Y, Z are integer coordinates.
 *
 * @since 2.0.0
 */
typealias FormattedBlockVector =
    @UseSerializer(FormattedBlockVectorSerializer::class)
    BlockVector

/**
 * Serializer for converting [BlockVector] objects to and from string format.
 *
 * String format: "X, Y, Z"
 * - X, Y, Z: Integer values representing block coordinates
 * - Commas separate coordinates
 * - Whitespace around values is trimmed
 *
 * @throws InvalidFormatException if the string format is invalid
 * @since 2.0.0
 */
object FormattedBlockVectorSerializer : TransformSerializer.Keyable<BlockVector, String>(StringSerializer) {
    override fun transform(value: String): BlockVector {
        val split = value.split("\\s*,\\s*".toRegex(), limit = 3)
        if (split.size != 3) {
            throw InvalidFormatException(value, "X, Y, Z")
        }
        return BlockVector(
            split[0].toInt(),
            split[1].toInt(),
            split[2].toInt(),
        )
    }

    override fun transformBack(value: BlockVector) = "${value.x},${value.y},${value.z}"
}
