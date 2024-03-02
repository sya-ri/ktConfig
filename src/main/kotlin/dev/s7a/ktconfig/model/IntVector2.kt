package dev.s7a.ktconfig.model

import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import kotlin.reflect.typeOf

/**
 * Two-dimensional integer vector.
 *
 * @since 1.0.0
 */
@UseSerializer(IntVector2.Serializer::class)
data class IntVector2(val x: Int, val y: Int) {
    /**
     * Serializer of [IntVector2] separated by commas.
     *
     * @since 1.0.0
     */
    object Serializer : KtConfigSerializer<String, IntVector2> {
        override val type = typeOf<String>()

        override fun deserialize(value: String): IntVector2? {
            return runCatching {
                value.split(',').let {
                    if (it.size != 2) return null
                    val x = it[0].trim().toInt()
                    val y = it[1].trim().toInt()
                    IntVector2(x, y)
                }
            }.getOrNull()
        }

        override fun serialize(value: IntVector2): String {
            return "${value.x}, ${value.y}"
        }
    }
}
