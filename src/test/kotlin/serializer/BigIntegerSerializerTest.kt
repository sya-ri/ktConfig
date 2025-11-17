package serializer

import dev.s7a.ktconfig.serializer.BigIntegerSerializer
import testSerializer
import java.math.BigInteger
import kotlin.test.Test

class BigIntegerSerializerTest {
    @Test
    fun testBasic() =
        testSerializer(
            BigInteger("123"),
            BigIntegerSerializer,
            expectedYaml =
                """
                test: '123'

                """.trimIndent(),
        )

    @Test
    fun testZero() =
        testSerializer(
            BigInteger("0"),
            BigIntegerSerializer,
            expectedYaml =
                """
                test: '0'

                """.trimIndent(),
        )

    @Test
    fun testNegative() =
        testSerializer(
            BigInteger("-123"),
            BigIntegerSerializer,
            expectedYaml =
                """
                test: '-123'

                """.trimIndent(),
        )

    @Test
    fun testLargeNumber() =
        testSerializer(
            BigInteger("12345678901234567890"),
            BigIntegerSerializer,
            expectedYaml =
                """
                test: '12345678901234567890'

                """.trimIndent(),
        )
}
