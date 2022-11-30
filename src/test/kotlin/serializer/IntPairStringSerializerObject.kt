package serializer

import dev.s7a.ktconfig.KtConfigSerializer
import kotlin.reflect.typeOf

object IntPairStringSerializerObject : KtConfigSerializer {
    override val type = typeOf<String>()

    override fun deserialize(value: Any?): Any? {
        if (value !is String) return null
        return value.split(',').run {
            if (size != 2) return null
            val first = this[0].trim().toIntOrNull() ?: return null
            val second = this[1].trim().toIntOrNull() ?: return null
            first to second
        }
    }

    override fun serialize(value: Any?): Any? {
        if (value !is Pair<*, *>) return null
        return "${value.first}, ${value.second}"
    }
}
