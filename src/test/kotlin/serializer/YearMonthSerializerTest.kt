package serializer

import dev.s7a.ktconfig.serializer.YearMonthSerializer
import testSerializer
import java.time.YearMonth
import kotlin.test.Test

class YearMonthSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            YearMonth.of(2026, 5),
            YearMonthSerializer,
        )
}
