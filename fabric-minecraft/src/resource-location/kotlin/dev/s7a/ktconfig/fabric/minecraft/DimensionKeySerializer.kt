package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level

/**
 * Serializer for converting dimension [ResourceKey] values to and from `namespace:path` resource location strings.
 *
 * This serializer can also be used for map keys.
 *
 * @throws InvalidFormatException if the value is not a valid resource location
 * @since 2.3.0
 */
object DimensionKeySerializer :
    TransformSerializer<ResourceKey<Level>, String>(StringSerializer),
    Serializer.Keyable<ResourceKey<Level>> {
    override fun decode(value: String): ResourceKey<Level> =
        ResourceKey.create(Registries.DIMENSION, ResourceLocationSerializer.decode(value))

    override fun encode(value: ResourceKey<Level>) = value.location().toString()
}
