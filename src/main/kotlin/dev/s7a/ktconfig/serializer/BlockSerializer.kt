package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.InvalidBlockFormatException
import dev.s7a.ktconfig.exception.WorldNotFoundException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.block.Block

object BlockSerializer : KSerializer<Block> {
    override val descriptor = PrimitiveSerialDescriptor("org.bukkit.Block", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Block {
        return decoder.decodeString().split("\\s*,\\s*".toRegex()).let {
            try {
                if (it.size == 4) {
                    val worldName = it[0].trim()
                    val world = Bukkit.getWorld(worldName) ?: throw WorldNotFoundException(worldName)
                    world.getBlockAt(it[1].toInt(), it[2].toInt(), it[3].toInt())
                } else {
                    throw InvalidBlockFormatException(it.joinToString())
                }
            } catch (ex: NumberFormatException) {
                throw InvalidBlockFormatException(it.joinToString())
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Block) {
        encoder.encodeString("${value.world.name}, ${value.x}, ${value.y}, ${value.z}")
    }
}
