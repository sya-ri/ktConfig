package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.Chunk
import kotlin.reflect.typeOf

/**
 * [Chunk] separated by commas.
 *
 * @since 1.0.0
 * @see Chunk
 */
typealias ChunkString = @UseSerializer(ChunkStringSerializer::class) Chunk

/**
 * Serializer of [Chunk] separated by commas.
 *
 * @since 1.0.0
 */
object ChunkStringSerializer : KtConfigSerializer {
    override val type = typeOf<String>()

    override fun deserialize(value: Any?): Chunk? {
        require(value is String)
        return runCatching {
            value.split(',').let {
                if (it.size != 3) return null
                val world = Bukkit.getWorld(it[0].trim()) ?: return null
                val x = it[1].trim().toInt()
                val z = it[2].trim().toInt()
                world.getChunkAt(x, z)
            }
        }.getOrNull()
    }

    override fun serialize(value: Any?): String {
        require(value is Chunk)
        return "${value.world.name}, ${value.x}, ${value.z}"
    }
}
