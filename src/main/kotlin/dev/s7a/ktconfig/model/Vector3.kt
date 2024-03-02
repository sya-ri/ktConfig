package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import kotlin.reflect.typeOf

/**
 * Three-dimensional vector.
 *
 * @since 1.0.0
 */
@UseSerializer(Vector3.Serializer::class)
data class Vector3(val x: Double, val y: Double, val z: Double) {
    /**
     * Serializer of [Vector3] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer<String, Vector3> {
        override val type = typeOf<String>()

        override fun deserialize(value: String): Vector3? {
            return runCatching {
                value.split(',').let {
                    if (it.size != 3) return null
                    val x = it[0].trim().toDouble()
                    val y = it[1].trim().toDouble()
                    val z = it[2].trim().toDouble()
                    Vector3(x, y, z)
                }
            }.getOrNull()
        }

        override fun serialize(value: Vector3): String {
            return "${value.x}, ${value.y}, ${value.z}"
        }
    }
}
