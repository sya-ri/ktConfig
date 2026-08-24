package serializer

import dev.s7a.ktconfig.serializer.LocalTimeSerializer
import testSerializer
import java.time.LocalTime
import kotlin.test.Test

class LocalTimeSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            LocalTime.of(12, 34, 56),
            LocalTimeSerializer,
        )
}
