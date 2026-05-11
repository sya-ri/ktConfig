package serializer

import dev.s7a.ktconfig.exception.NullValueException
import dev.s7a.ktconfig.serializer.ListSerializer
import dev.s7a.ktconfig.serializer.StringSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CollectionSerializerTest {
    @Test
    fun testDeserializeScalarAsSingleElementCollection() {
        assertEquals(listOf("value"), ListSerializer(StringSerializer).deserialize("value"))
    }

    @Test
    fun testDeserializeNullElementThrows() {
        assertFailsWith<NullValueException> {
            ListSerializer(StringSerializer).deserialize(listOf("value", null))
        }
    }

    @Test
    fun testNullableDeserializeKeepsNullElement() {
        assertEquals(listOf("value", null), ListSerializer.Nullable(StringSerializer).deserialize(listOf("value", null)))
    }
}
