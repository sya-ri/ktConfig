package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.exception.InvalidLocationFormatException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    override val descriptor = PrimitiveSerialDescriptor("org.bukkit.Location", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Location {
        return decoder.decodeString().split(',').let {
            try {
                when (it.size) {
                    4 -> {
                        val world = Bukkit.getWorld(it[0].trim())
                        Location(world, it[1].toDouble(), it[2].toDouble(), it[3].toDouble())
                    }
                    6 -> {
                        val world = Bukkit.getWorld(it[0].trim())
                        Location(world, it[1].toDouble(), it[2].toDouble(), it[3].toDouble(), it[4].toFloat(), it[5].toFloat())
                    }
                    else -> throw InvalidLocationFormatException(it.joinToString())
                }
            } catch (ex: NumberFormatException) {
                throw InvalidLocationFormatException(it.joinToString())
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Location) {
        if (value.yaw == 0F && value.pitch == 0F) {
            encoder.encodeString("${value.world?.name}, ${value.x}, ${value.y}, ${value.z}")
        } else {
            encoder.encodeString("${value.world?.name}, ${value.x}, ${value.y}, ${value.z}, ${value.yaw}, ${value.pitch}")
        }
    }
}
