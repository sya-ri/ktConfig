package serializer

import dev.s7a.ktconfig.serializer.InstantSerializer
import testSerializer
import java.time.Instant
import kotlin.test.Test

class InstantSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            Instant.parse("2026-05-11T12:34:56Z"),
            InstantSerializer,
        )
}
