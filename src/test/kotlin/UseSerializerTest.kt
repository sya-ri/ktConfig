import dev.s7a.ktconfig.KtConfigSerializer
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class UseSerializerTest {
    private object IntPairStringSerializerObject : KtConfigSerializer {
        override val type = typeOf<String>()

        override fun deserialize(value: Any?): Any? {
            if (value !is String) return null
            return value.split(',').run {
                if (size != 2) return null
                val first = this[0].toIntOrNull() ?: return null
                val second = this[1].toIntOrNull() ?: return null
                first to second
            }
        }

        override fun serialize(value: Any?): Any? {
            if (value !is Pair<*, *>) return null
            return "${value.first}, ${value.second}"
        }
    }

    private class IntPairStringSerializerClass : KtConfigSerializer {
        override val type = typeOf<String>()

        override fun deserialize(value: Any?): Any? {
            if (value !is String) return null
            return value.split(',').run {
                if (size != 2) return null
                val first = this[0].toIntOrNull() ?: return null
                val second = this[1].toIntOrNull() ?: return null
                first to second
            }
        }

        override fun serialize(value: Any?): Any? {
            if (value !is Pair<*, *>) return null
            return "${value.first}, ${value.second}"
        }
    }

    @Test
    fun use_object() {
        data class Data(val data: @UseSerializer(with = IntPairStringSerializerObject::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }

    @Test
    fun use_class() {
        data class Data(val data: @UseSerializer(with = IntPairStringSerializerClass::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }
}
