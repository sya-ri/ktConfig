package serializer

import dev.s7a.ktconfig.KtConfigSerializer
import kotlin.reflect.typeOf

class IntPairStringSerializerClass : KtConfigSerializer<String, Pair<Int, Int>> {
    override val type = typeOf<String>()

    override fun deserialize(value: String): Pair<Int, Int>? {
        return value.split(',').run {
            if (size != 2) return null
            val first = this[0].trim().toIntOrNull() ?: return null
            val second = this[1].trim().toIntOrNull() ?: return null
            first to second
        }
    }

    override fun serialize(value: Pair<Int, Int>): String {
        return "${value.first}, ${value.second}"
    }
}
