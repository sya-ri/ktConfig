package serializer

import dev.s7a.ktconfig.serializer.YearSerializer
import testSerializer
import java.time.Year
import kotlin.test.Test

class YearSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            Year.of(2026),
            YearSerializer,
        )
}
