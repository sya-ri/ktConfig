package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.reflect.typeOf

/**
 * [Location] separated by commas.
 *
 * @since 1.0.0
 * @see Location
 */
@Suppress("ktlint:annotation")
typealias LocationString = @UseSerializer(LocationStringSerializer::class) Location

/**
 * Serializer of [Location] separated by commas.
 *
 * @since 1.0.0
 */
object LocationStringSerializer : KtConfigSerializer {
    override val type = typeOf<String>()

    override fun deserialize(value: Any?): Location? {
        require(value is String)
        return runCatching {
            value.split(',').let {
                if (it.size !in 4..6) return null
                val world = Bukkit.getWorld(it[0].trim())
                val x = it[1].trim().toDouble()
                val y = it[2].trim().toDouble()
                val z = it[3].trim().toDouble()
                val yaw = it.getOrNull(4)?.trim()?.toFloat() ?: 0F
                val pitch = it.getOrNull(5)?.trim()?.toFloat() ?: 0F
                Location(world, x, y, z, yaw, pitch)
            }
        }.getOrNull()
    }

    override fun serialize(value: Any?): String {
        require(value is Location)
        return if (value.yaw == 0F && value.pitch == 0F) {
            "${value.world?.name}, ${value.x}, ${value.y}, ${value.z}"
        } else {
            "${value.world?.name}, ${value.x}, ${value.y}, ${value.z}, ${value.yaw}, ${value.pitch}"
        }
    }
}
