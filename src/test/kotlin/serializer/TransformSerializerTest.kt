package serializer

import dev.s7a.ktconfig.serializer.StringSerializer
import dev.s7a.ktconfig.serializer.TransformSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformSerializerTest {
    private data class Value(
        val content: String,
    )

    private object ValueSerializer : TransformSerializer<Value, String>(StringSerializer) {
        override fun decode(value: String) = Value(value.removePrefix("value:"))

        override fun encode(value: Value) = "value:${value.content}"
    }

    @Test
    fun testDeserializeUsesBaseSerializerAndDecode() {
        assertEquals(Value("content"), ValueSerializer.deserialize("value:content"))
    }

    @Test
    fun testSerializeUsesEncodeAndBaseSerializer() {
        assertEquals("value:content", ValueSerializer.serialize(Value("content")))
    }
}
