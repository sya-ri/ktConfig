package dev.s7a.ktconfig.serializer

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import org.bukkit.util.Vector
import kotlin.reflect.typeOf

/**
 * [Vector] separated by commas.
 *
 * @since 1.0.0
 * @see Vector
 */
@Suppress("ktlint:standard:annotation")
typealias VectorString = @UseSerializer(VectorStringSerializer::class) Vector

/**
 * Serializer of [Vector] separated by commas.
 *
 * @since 1.0.0
 */
object VectorStringSerializer : KtConfigSerializer<String, Vector> {
    override val type = typeOf<String>()

    override fun deserialize(value: String): Vector? {
        return runCatching {
            value.split(',').let {
                if (it.size != 3) return null
                val x = it[0].trim().toDouble()
                val y = it[1].trim().toDouble()
                val z = it[2].trim().toDouble()
                return Vector(x, y, z)
            }
        }.getOrNull()
    }

    override fun serialize(value: Vector): String {
        return "${value.x}, ${value.y}, ${value.z}"
    }
}
