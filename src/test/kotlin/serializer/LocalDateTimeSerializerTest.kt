package serializer

import dev.s7a.ktconfig.serializer.LocalDateTimeSerializer
import testSerializer
import java.time.LocalDateTime
import kotlin.test.Test

class LocalDateTimeSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            LocalDateTime.of(2026, 5, 11, 12, 34, 56),
            LocalDateTimeSerializer,
        )
}
