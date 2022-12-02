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
@Suppress("ktlint:annotation")
typealias VectorString = @UseSerializer(VectorStringSerializer::class) Vector

/**
 * Serializer of [Vector] separated by commas.
 *
 * @since 1.0.0
 */
object VectorStringSerializer : KtConfigSerializer {
    override val type = typeOf<String>()

    override fun deserialize(value: Any?): Vector? {
        require(value is String)
        return runCatching {
            value.split(',').let {
                val x = it[0].trim().toDouble()
                val y = it[1].trim().toDouble()
                val z = it[2].trim().toDouble()
                when (it.size) {
                    3 -> {
                        Vector(x, y, z)
                    }
                    else -> {
                        null
                    }
                }
            }
        }.getOrNull()
    }

    override fun serialize(value: Any?): String {
        require(value is Vector)
        return "${value.x}, ${value.y}, ${value.z}"
    }
}
