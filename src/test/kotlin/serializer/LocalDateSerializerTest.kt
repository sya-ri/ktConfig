package serializer

import dev.s7a.ktconfig.serializer.LocalDateSerializer
import testSerializer
import java.time.LocalDate
import kotlin.test.Test

class LocalDateSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            LocalDate.of(2026, 5, 11),
            LocalDateSerializer,
        )
}
