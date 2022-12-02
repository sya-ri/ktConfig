
import dev.s7a.ktconfig.UseSerializer
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import serializer.IntPairStringSerializerClass
import serializer.IntPairStringSerializerObject
import serializer.UByteUByteSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class UseSerializerTest {
    @Test
    fun use_object() {
        @Suppress("ktlint:annotation")
        data class Data(val data: @UseSerializer(IntPairStringSerializerObject::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }

    @Test
    fun use_class() {
        @Suppress("ktlint:annotation")
        data class Data(val data: @UseSerializer(IntPairStringSerializerClass::class) Pair<Int, Int>)

        assertEquals("data: 1, 2\n", saveKtConfigString(Data(1 to 2)))
        assertEquals(Data(2 to 3), ktConfigString("data: 2, 3"))
    }

    @Test
    fun map() {
        data class Data(val data: Map<@UseSerializer(IntPairStringSerializerClass::class) Pair<Int, Int>, Int>)

        assertEquals(
            """
                data:
                  1, 2: 3
                  3, 4: 5
                
            """.trimIndent(),
            saveKtConfigString(Data(mapOf((1 to 2) to 3, (3 to 4) to 5)))
        )
        assertEquals(
            Data(mapOf((1 to 3) to 5, (2 to 4) to 6)),
            ktConfigString(
                """
                    data:
                      1, 3: 5
                      2, 4: 6
                """.trimIndent()
            )
        )
    }

    // UByte is supported by ktConfig and is not JavaBean.
    @Test
    fun ubyte_ubyte() {
        @Suppress("ktlint:annotation")
        data class Data(val data: @UseSerializer(UByteUByteSerializer::class) UByte)

        assertEquals("data: 1\n", saveKtConfigString(Data(1U)))
        assertEquals(Data(2U), ktConfigString("data: 2"))
    }
}
