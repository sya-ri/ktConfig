package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.Bukkit
import org.bukkit.block.Block

/**
 * Type alias for [Block] that uses [FormattedBlockSerializer] for string-based serialization.
 *
 * Represents a block with format: "World, X, Y, Z"
 * where World is the name of the world and X, Y, Z are integer coordinates.
 *
 * @since 2.0.0
 */
typealias FormattedBlock =
    @UseSerializer(FormattedBlockSerializer::class)
    Block

/**
 * Serializer for converting [Block] objects to and from string format.
 *
 * String format: "World, X, Y, Z"
 * - World: The name of the world
 * - X, Y, Z: Integer values representing block coordinates
 * - Commas separate coordinates
 * - Whitespace around values is trimmed
 *
 * @throws RuntimeException if the world is not found
 * @since 2.0.0
 */
object FormattedBlockSerializer : TransformSerializer.Keyable<Block, String>(StringSerializer) {
    override fun decode(value: String): Block {
        val split = value.split("\\s*,\\s*".toRegex(), limit = 4)
        val world = Bukkit.getWorld(split[0]) ?: throw RuntimeException("World not found: ${split[0]}")
        return world.getBlockAt(
            split[1].toInt(),
            split[2].toInt(),
            split[3].toInt(),
        )
    }

    override fun encode(value: Block) = "${value.world.name}, ${value.x}, ${value.y}, ${value.z}"
}
