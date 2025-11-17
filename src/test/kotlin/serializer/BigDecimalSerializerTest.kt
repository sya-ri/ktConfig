package serializer

import dev.s7a.ktconfig.serializer.BigDecimalSerializer
import testSerializer
import java.math.BigDecimal
import kotlin.test.Test

class BigDecimalSerializerTest {
    @Test
    fun testBasic() =
        testSerializer(
            BigDecimal("123"),
            BigDecimalSerializer,
            expectedYaml =
                """
                test: '123'

                """.trimIndent(),
        )

    @Test
    fun testDecimalPlaces() =
        testSerializer(
            BigDecimal("123.456"),
            BigDecimalSerializer,
            expectedYaml =
                """
                test: '123.456'

                """.trimIndent(),
        )

    @Test
    fun testZero() =
        testSerializer(
            BigDecimal("0"),
            BigDecimalSerializer,
            expectedYaml =
                """
                test: '0'

                """.trimIndent(),
        )

    @Test
    fun testNegative() =
        testSerializer(
            BigDecimal("-123.456"),
            BigDecimalSerializer,
            expectedYaml =
                """
                test: '-123.456'

                """.trimIndent(),
        )

    @Test
    fun testLargeNumber() =
        testSerializer(
            BigDecimal("12345678901234567890.123456789"),
            BigDecimalSerializer,
            expectedYaml =
                """
                test: '12345678901234567890.123456789'

                """.trimIndent(),
        )
}
