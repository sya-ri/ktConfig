package serializer

import dev.s7a.ktconfig.serializer.OffsetTimeSerializer
import testSerializer
import java.time.OffsetTime
import kotlin.test.Test

class OffsetTimeSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            OffsetTime.parse("12:34:56+09:00"),
            OffsetTimeSerializer,
        )
}
