package serializer

import dev.s7a.ktconfig.exception.InvalidFormatException
import dev.s7a.ktconfig.serializer.DateSerializer
import testSerializer
import java.util.Calendar
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DateSerializerTest {
    private fun testDateSerializer(
        timeZone: String,
        block: Calendar.() -> Unit,
        input: String,
        output: String,
    ) {
        val expected = Calendar.getInstance(TimeZone.getTimeZone(timeZone)).apply(block).time
        testSerializer(expected, DateSerializer, expectedYaml = output)
        assertEquals(expected, DateSerializer.deserialize(input))
    }

    @Test
    fun `serialize UTC timestamp without time`() {
        testDateSerializer(
            "UTC",
            {
                set(2023, 11, 31, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            },
            "2023-12-31",
            """
            test: 2023-12-31T00:00:00Z
            
            """.trimIndent(),
        )
    }

    @Test
    fun `serialize UTC timestamp with zero milliseconds`() {
        testDateSerializer(
            "UTC",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 0)
            },
            "2023-12-31T23:59:59",
            """
            test: 2023-12-31T23:59:59Z
            
            """.trimIndent(),
        )
    }

    @Test
    fun `serialize UTC timestamp with milliseconds`() {
        // .999
        testDateSerializer(
            "UTC",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            },
            "2023-12-31T23:59:59.999",
            """
            test: 2023-12-31T23:59:59.999Z
            
            """.trimIndent(),
        )

        // .990
        testDateSerializer(
            "UTC",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 990)
            },
            "2023-12-31T23:59:59.99",
            """
            test: 2023-12-31T23:59:59.990Z
            
            """.trimIndent(),
        )

        // .900
        testDateSerializer(
            "UTC",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 900)
            },
            "2023-12-31T23:59:59.9",
            """
            test: 2023-12-31T23:59:59.900Z
            
            """.trimIndent(),
        )
    }

    @Test
    fun `serialize Asia Tokyo timezone`() {
        testDateSerializer(
            "Asia/Tokyo",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 0)
            },
            // 12/31 23:59:59 -> 12/31 14:59:59 (+9:00)
            "2023-12-31T23:59:59+09:00",
            """
            test: 2023-12-31T14:59:59Z
            
            """.trimIndent(),
        )
    }

    @Test
    fun `serialize America Atka timezone`() {
        testDateSerializer(
            "America/Atka",
            {
                set(2023, 11, 31, 23, 59, 59)
                set(Calendar.MILLISECOND, 0)
            },
            // 12/31 23:59:59 -> 1/1 09:59:59 (-10:00)
            "2023-12-31T23:59:59-10:00",
            """
            test: 2024-01-01T09:59:59Z
            
            """.trimIndent(),
        )
    }

    @Test
    fun testInvalidFormat() {
        assertFailsWith<InvalidFormatException> {
            DateSerializer.deserialize("invalid-date")
        }.apply {
            assertEquals("Invalid format: invalid-date, expected: yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss", message)
        }
    }
}
