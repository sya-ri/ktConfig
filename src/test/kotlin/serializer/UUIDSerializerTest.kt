package serializer

import dev.s7a.ktconfig.serializer.UUIDSerializer
import testSerializer
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UUIDSerializerTest {
    @Test
    fun testNormal() = testSerializer(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), UUIDSerializer)

    @Test
    fun testEmpty() = testSerializer(UUID(0, 0), UUIDSerializer)

    @Test
    fun testInvalid() {
        assertFailsWith<IllegalArgumentException> {
            UUIDSerializer.decode("not-a-uuid")
        }
    }
}
