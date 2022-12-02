package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.block.Block
import kotlin.reflect.typeOf

/**
 * [Block] separated by commas.
 *
 * @since 1.0.0
 * @see Block
 */
@Suppress("ktlint:annotation")
typealias BlockString = @UseSerializer(BlockStringSerializer::class) Block

/**
 * Serializer of [Block] separated by commas.
 *
 * @since 1.0.0
 */
object BlockStringSerializer : KtConfigSerializer {
    override val type = typeOf<String>()

    override fun deserialize(value: Any?): Block? {
        require(value is String)
        return runCatching {
            value.split(',').let {
                if (it.size != 4) return null
                val world = Bukkit.getWorld(it[0].trim()) ?: return null
                val x = it[1].trim().toInt()
                val y = it[2].trim().toInt()
                val z = it[3].trim().toInt()
                world.getBlockAt(x, y, z)
            }
        }.getOrNull()
    }

    override fun serialize(value: Any?): String {
        require(value is Block)
        return "${value.world.name}, ${value.x}, ${value.y}, ${value.z}"
    }
}
