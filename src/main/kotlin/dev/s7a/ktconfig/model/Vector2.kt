package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import kotlin.reflect.typeOf

/**
 * Two-dimensional vector.
 *
 * @since 1.0.0
 */
@UseSerializer(Vector2.Serializer::class)
data class Vector2(val x: Double, val y: Double) {
    /**
     * Serializer of [Vector2] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer<String, Vector2> {
        override val type = typeOf<String>()

        override fun deserialize(value: String): Vector2? {
            return runCatching {
                value.split(',').let {
                    if (it.size != 2) return null
                    val x = it[0].trim().toDouble()
                    val y = it[1].trim().toDouble()
                    Vector2(x, y)
                }
            }.getOrNull()
        }

        override fun serialize(value: Vector2): String {
            return "${value.x}, ${value.y}"
        }
    }
}
