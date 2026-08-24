package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import net.minecraft.resources.ResourceLocation

/**
 * Serializer for converting [ResourceLocation] values to and from `namespace:path` strings.
 *
 * This serializer can also be used for map keys.
 *
 * @throws InvalidFormatException if the value is not a valid resource location
 * @since 2.3.0
 */
object ResourceLocationSerializer :
    TransformSerializer<ResourceLocation, String>(StringSerializer),
    Serializer.Keyable<ResourceLocation> {
    override fun decode(value: String): ResourceLocation =
        ResourceLocation.tryParse(value) ?: throw InvalidFormatException(value, "namespace:path")

    override fun encode(value: ResourceLocation) = value.toString()
}
