import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import serializer.IntPairStringSerializerClass
import serializer.IntPairStringSerializerObject
import kotlin.test.Test
import kotlin.test.assertEquals

class UseSerializerTest {
    @Test
    fun use_object() {
        data class Data(val data: @UseSerializer(IntPairStringSerializerObject::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }

    @Test
    fun use_class() {
        data class Data(val data: @UseSerializer(IntPairStringSerializerClass::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }
}
