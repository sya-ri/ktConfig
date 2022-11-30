package serializer

import dev.s7a.ktconfig.KtConfigSerializer
import kotlin.reflect.typeOf

class UByteUByteSerializer : KtConfigSerializer {
    override val type = typeOf<UByte>()

    override fun deserialize(value: Any?): Any? {
        return value as? UByte
    }

    override fun serialize(value: Any?): Any? {
        return value as? UByte
    }
}
