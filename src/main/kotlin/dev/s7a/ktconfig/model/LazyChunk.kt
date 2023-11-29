package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.Chunk
import kotlin.reflect.typeOf

/**
 * [Chunk] to lazily retrieve the world
 *
 * @see Chunk
 * @since 1.0.0
 */
@UseSerializer(LazyChunk.Serializer::class)
data class LazyChunk(val world: String, val x: Int, val z: Int) {
    companion object {
        /**
         * Generate [LazyChunk] from [Chunk].
         *
         * @since 1.0.0
         */
        fun from(chunk: Chunk): LazyChunk {
            return LazyChunk(chunk.world.name, chunk.x, chunk.z)
        }
    }

    /**
     * Get [Chunk]. If the world does not exist, returns null.
     *
     * @since 1.0.0
     */
    fun get(): Chunk? {
        return Bukkit.getWorld(world)?.getChunkAt(x, z)
    }

    /**
     * Serializer of [LazyChunk] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer<String, LazyChunk> {
        override val type = typeOf<String>()

        override fun deserialize(value: String): LazyChunk? {
            return runCatching {
                value.split(',').let {
                    if (it.size != 3) return null
                    val world = it[0].trim()
                    val x = it[1].trim().toInt()
                    val z = it[2].trim().toInt()
                    LazyChunk(world, x, z)
                }
            }.getOrNull()
        }

        override fun serialize(value: LazyChunk): String {
            return "${value.world}, ${value.x}, ${value.z}"
        }
    }
}
