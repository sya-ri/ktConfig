package serializer

import dev.s7a.ktconfig.serializer.NumberSerializer
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberSerializerTest {
    @Test
    fun testDeserializeNumber() {
        assertEquals(123, NumberSerializer.deserialize(123))
    }

    @Test
    fun testDeserializeDecimalString() {
        assertEquals(BigDecimal("123.456"), NumberSerializer.deserialize("123.456"))
    }

    @Test
    fun testDeserializePositiveInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, NumberSerializer.deserialize("Infinity"))
    }

    @Test
    fun testDeserializeNegativeInfinity() {
        assertEquals(Double.NEGATIVE_INFINITY, NumberSerializer.deserialize("-Infinity"))
    }

    @Test
    fun testDeserializeNaN() {
        assertTrue((NumberSerializer.deserialize("NaN") as Double).isNaN())
    }

    @Test
    fun testSerialize() {
        assertEquals("123.456", NumberSerializer.serialize(BigDecimal("123.456")))
    }
}
