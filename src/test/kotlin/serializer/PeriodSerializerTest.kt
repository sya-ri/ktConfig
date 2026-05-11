package serializer

import dev.s7a.ktconfig.serializer.PeriodSerializer
import testSerializer
import java.time.Period
import kotlin.test.Test

class PeriodSerializerTest {
    @Test
    fun testRoundTrip() =
        testSerializer(
            Period.parse("P1Y2M3D"),
            PeriodSerializer,
        )
}
