package serializer

import dev.s7a.ktconfig.exception.UnsupportedConvertException
import dev.s7a.ktconfig.serializer.BooleanSerializer
import testSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BooleanSerializerTest {
    @Test
    fun testTrue() = testSerializer(true, BooleanSerializer)

    @Test
    fun testFalse() = testSerializer(false, BooleanSerializer)

    @Test
    fun testString() {
        assertEquals(true, BooleanSerializer.deserialize("true"))
        assertEquals(false, BooleanSerializer.deserialize("false"))
    }

    @Test
    fun testUnsupported() {
        assertFailsWith<UnsupportedConvertException> {
            BooleanSerializer.deserialize(1)
        }.apply {
            assertEquals("Unsupported convert: kotlin.Int -> kotlin.Boolean", message)
        }
    }
}
