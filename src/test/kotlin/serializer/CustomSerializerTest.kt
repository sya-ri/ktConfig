package serializer

import dev.s7a.ktconfig.serializer.Serializer
import testSerializer
import kotlin.test.Test

class CustomSerializerTest {
    object CustomSerializer : Serializer.Keyable<String> {
        override fun deserialize(value: Any): String {
            require(value is String) { "CustomSerializer: value must be String" }
            return value.hexToByteArray().decodeToString()
        }

        override fun serialize(value: String): String = value.encodeToByteArray().toHexString()
    }

    @Test
    fun testCustomSerializer() =
        testSerializer(
            "Hello world",
            CustomSerializer,
            mapOf("test" to "48656c6c6f20776f726c64"), // "Hello world" -> "48656c6c6f20776f726c64"
        )
}
