package serializer

import dev.s7a.ktconfig.serializer.ZonedDateTimeSerializer
import testSerializer
import java.time.ZonedDateTime
import kotlin.test.Test

class ZonedDateTimeSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            ZonedDateTime.parse("2026-05-11T12:34:56+09:00[Asia/Tokyo]"),
            ZonedDateTimeSerializer,
        )
}
