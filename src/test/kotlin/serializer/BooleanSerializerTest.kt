package serializer

import dev.s7a.ktconfig.serializer.BooleanSerializer
import testSerializer
import kotlin.test.Test

class BooleanSerializerTest {
    @Test
    fun testTrue() = testSerializer(true, BooleanSerializer)

    @Test
    fun testFalse() = testSerializer(false, BooleanSerializer)
}

