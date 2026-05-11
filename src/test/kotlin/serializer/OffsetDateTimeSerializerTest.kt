package serializer

import dev.s7a.ktconfig.serializer.OffsetDateTimeSerializer
import testSerializer
import java.time.OffsetDateTime
import kotlin.test.Test

class OffsetDateTimeSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            OffsetDateTime.parse("2026-05-11T12:34:56+09:00"),
            OffsetDateTimeSerializer,
        )
}
