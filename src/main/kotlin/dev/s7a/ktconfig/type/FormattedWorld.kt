package dev.s7a.ktconfig.type

import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import org.bukkit.Bukkit
import org.bukkit.World

/**
 * Type alias for [World] that uses [FormattedWorldSerializer] for string-based serialization.
 *
 * Represents a world with format: "WorldName"
 * where WorldName is the name of the world.
 *
 * @since 2.0.0
 */
typealias FormattedWorld =
    @UseSerializer(FormattedWorldSerializer::class)
    World

/**
 * Serializer for converting [World] objects to and from string format.
 *
 * String format: "WorldName"
 * - WorldName: The name of the world
 *
 * @throws RuntimeException if the world is not found
 * @since 2.0.0
 */
object FormattedWorldSerializer : TransformSerializer.Keyable<World, String>(StringSerializer) {
    override fun transform(value: String) = Bukkit.getWorld(value) ?: throw RuntimeException("World not found: $value")

    override fun transformBack(value: World) = value.name
}
