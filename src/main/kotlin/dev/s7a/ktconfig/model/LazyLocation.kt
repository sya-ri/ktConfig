package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.reflect.typeOf

/**
 * [Location] to lazily retrieve the world
 *
 * @see Location
 * @since 1.0.0
 */
@UseSerializer(LazyLocation.Serializer::class)
data class LazyLocation(val world: String, val x: Double, val y: Double, val z: Double, val yaw: Float = 0F, val pitch: Float = 0F) {
    companion object {
        /**
         * Generate [LazyLocation] from [Location]. If [Location.getWorld] is null, returns null.
         *
         * @since 1.0.0
         */
        fun from(location: Location): LazyLocation? {
            return location.world?.name?.let { LazyLocation(it, location.x, location.y, location.z, location.yaw, location.pitch) }
        }
    }

    /**
     * Get [Location]. If the world does not exist, [Location.getWorld] will be null.
     *
     * @since 1.0.0
     */
    fun get(): Location {
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

    /**
     * Serializer of [LazyLocation] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer {
        override val type = typeOf<String>()

        override fun deserialize(value: Any?): LazyLocation? {
            require(value is String)
            return runCatching {
                value.split(',').let {
                    if (it.size !in 4..6) return null
                    val world = it[0].trim()
                    val x = it[1].trim().toDouble()
                    val y = it[2].trim().toDouble()
                    val z = it[3].trim().toDouble()
                    val yaw = it.getOrNull(4)?.trim()?.toFloat() ?: 0F
                    val pitch = it.getOrNull(5)?.trim()?.toFloat() ?: 0F
                    LazyLocation(world, x, y, z, yaw, pitch)
                }
            }.getOrNull()
        }

        override fun serialize(value: Any?): String {
            require(value is LazyLocation)
            return if (value.yaw == 0F && value.pitch == 0F) {
                "${value.world}, ${value.x}, ${value.y}, ${value.z}"
            } else {
                "${value.world}, ${value.x}, ${value.y}, ${value.z}, ${value.yaw}, ${value.pitch}"
            }
        }
    }
}
