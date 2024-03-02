package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import kotlin.reflect.typeOf

/**
 * Three-dimensional integer vector.
 *
 * @since 1.0.0
 */
@UseSerializer(IntVector3.Serializer::class)
data class IntVector3(val x: Int, val y: Int, val z: Int) {
    /**
     * Serializer of [IntVector3] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer<String, IntVector3> {
        override val type = typeOf<String>()

        override fun deserialize(value: String): IntVector3? {
            return runCatching {
                value.split(',').let {
                    if (it.size != 3) return null
                    val x = it[0].trim().toInt()
                    val y = it[1].trim().toInt()
                    val z = it[2].trim().toInt()
                    IntVector3(x, y, z)
                }
            }.getOrNull()
        }

        override fun serialize(value: IntVector3): String {
            return "${value.x}, ${value.y}, ${value.z}"
        }
    }
}
