package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.block.Block
import kotlin.reflect.typeOf

/**
 * [Block] to lazily retrieve the world
 *
 * @see Block
 * @since 1.0.0
 */
@UseSerializer(LazyBlock.Serializer::class)
data class LazyBlock(val world: String, val x: Int, val y: Int, val z: Int) {
    companion object {
        /**
         * Generate [LazyBlock] from [Block].
         *
         * @since 1.0.0
         */
        fun from(block: Block): LazyBlock {
            return LazyBlock(block.world.name, block.x, block.y, block.z)
        }
    }

    /**
     * Get [Block]. If the world does not exist, returns null.
     *
     * @since 1.0.0
     */
    fun get(): Block? {
        return Bukkit.getWorld(world)?.getBlockAt(x, y, z)
    }

    /**
     * Serializer of [LazyBlock] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer {
        override val type = typeOf<String>()

        override fun deserialize(value: Any?): LazyBlock? {
            require(value is String)
            return runCatching {
                value.split(',').let {
                    if (it.size != 4) return null
                    val world = it[0].trim()
                    val x = it[1].trim().toInt()
                    val y = it[2].trim().toInt()
                    val z = it[3].trim().toInt()
                    LazyBlock(world, x, y, z)
                }
            }.getOrNull()
        }

        override fun serialize(value: Any?): String {
            require(value is LazyBlock)
            return "${value.world}, ${value.x}, ${value.y}, ${value.z}"
        }
    }
}
