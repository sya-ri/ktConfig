package serializer

import dev.s7a.ktconfig.KtConfigSerializer
import kotlin.reflect.typeOf

class StringSerializerForNesting : KtConfigSerializer<String, String> {
    override val type = typeOf<String>()

    override fun deserialize(value: String): String {
        return value.split("_").joinToString("")
    }

    override fun serialize(value: String): String {
        return value.toCharArray().joinToString("_")
    }
}
