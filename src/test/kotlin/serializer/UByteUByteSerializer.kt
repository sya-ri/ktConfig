package serializer

import dev.s7a.ktconfig.KtConfigSerializer
import kotlin.reflect.typeOf

class UByteUByteSerializer : KtConfigSerializer<UByte, UByte> {
    override val type = typeOf<UByte>()

    override fun deserialize(value: UByte): UByte {
        return value
    }

    override fun serialize(value: UByte): UByte {
        return value
    }
}
