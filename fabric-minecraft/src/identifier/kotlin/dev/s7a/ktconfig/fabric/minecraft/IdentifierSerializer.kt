package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import net.minecraft.resources.Identifier

/**
 * Serializer for converting [Identifier] values to and from `namespace:path` strings.
 *
 * This serializer can also be used for map keys.
 *
 * @throws InvalidFormatException if the value is not a valid identifier
 * @since 2.3.0
 */
object IdentifierSerializer :
    TransformSerializer<Identifier, String>(StringSerializer),
    Serializer.Keyable<Identifier> {
    override fun decode(value: String): Identifier = Identifier.tryParse(value) ?: throw InvalidFormatException(value, "namespace:path")

    override fun encode(value: Identifier) = value.toString()
}
