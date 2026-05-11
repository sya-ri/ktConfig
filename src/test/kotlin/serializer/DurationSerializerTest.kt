package serializer

import dev.s7a.ktconfig.serializer.DurationSerializer
import testSerializer
import java.time.Duration
import kotlin.test.Test

class DurationSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            Duration.parse("PT1H2M3S"),
            DurationSerializer,
        )
}
