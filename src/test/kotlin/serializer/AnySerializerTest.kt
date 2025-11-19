package serializer

import dev.s7a.ktconfig.serializer.AnySerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class AnySerializerTest {
    data class Data(
        val value: Int,
    )

    @Test
    fun testPassValue() {
        val value = Data(1)

        assertEquals(value, AnySerializer.serialize(value))
        assertEquals(value, AnySerializer.deserialize(value))
    }
}
