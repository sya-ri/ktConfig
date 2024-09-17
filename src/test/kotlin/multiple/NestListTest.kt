package multiple

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID

@Suppress("unused")
class NestListTest :
    FunSpec({
        data class Data(
            val string: String,
            val int: Int,
            val uint: UInt,
            val boolean: Boolean,
            val double: Double,
            val float: Float,
            val long: Long,
            val ulong: ULong,
            val byte: Byte,
            val ubyte: UByte,
            val char: Char,
            val short: Short,
            val ushort: UShort,
            val bigInteger: BigInteger,
            val bigDecimal: BigDecimal,
            val date: Date,
            val calendar: Calendar,
            val uuid: UUID,
        )

        data class Config(
            val data: List<Data>,
        )

        test("should get all values from nested list config") {
            ktConfigString<Config>(
                """
                data:
                  - string: config string 1
                    int: 123
                    uint: 123
                    boolean: true
                    double: 123.45
                    float: 123.45
                    long: 1234567890123456789
                    ulong: 1234567890123456789
                    byte: 127
                    ubyte: 255
                    char: c
                    short: 12345
                    ushort: 12345
                    bigInteger: 1234567890123456789012345678901234567890
                    bigDecimal: '12345.67890'
                    date: 2020-12-03T10:35:30Z
                    calendar: 2020-12-03T10:35:30Z
                    uuid: 123e4567-e89b-12d3-a456-426655440000
                  - string: config string 2
                    int: 456
                    uint: 456
                    boolean: false
                    double: 678.90
                    float: 678.90
                    long: 987654321012345678
                    ulong: 987654321012345678
                    byte: 126
                    ubyte: 254
                    char: d
                    short: 23456
                    ushort: 23456
                    bigInteger: 9876543210123456789012345678901234567890
                    bigDecimal: '67890.12345'
                    date: 2021-01-01T00:00:00Z
                    calendar: 2021-01-01T00:00:00Z
                    uuid: 123e4567-e89b-12d3-a456-426655440001
                """.trimIndent(),
            ) shouldBe
                Config(
                    data =
                        listOf(
                            Data(
                                string = "config string 1",
                                int = 123,
                                uint = 123u,
                                boolean = true,
                                double = 123.45,
                                float = 123.45f,
                                long = 1234567890123456789L,
                                ulong = 1234567890123456789uL,
                                byte = 127,
                                ubyte = 255u,
                                char = 'c',
                                short = 12345,
                                ushort = 12345u,
                                bigInteger = BigInteger("1234567890123456789012345678901234567890"),
                                bigDecimal = BigDecimal("12345.67890"),
                                date = Date(1606991730000L),
                                calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                                uuid = UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            ),
                            Data(
                                string = "config string 2",
                                int = 456,
                                uint = 456u,
                                boolean = false,
                                double = 678.90,
                                float = 678.90f,
                                long = 987654321012345678L,
                                ulong = 987654321012345678uL,
                                byte = 126,
                                ubyte = 254u,
                                char = 'd',
                                short = 23456,
                                ushort = 23456u,
                                bigInteger = BigInteger("9876543210123456789012345678901234567890"),
                                bigDecimal = BigDecimal("67890.12345"),
                                date = Date(1609459200000L),
                                calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                                uuid = UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                            ),
                        ),
                )
        }

        test("should save all values to nested list config") {
            saveKtConfigString(
                Config(
                    data =
                        listOf(
                            Data(
                                string = "config string 1",
                                int = 123,
                                uint = 123u,
                                boolean = true,
                                double = 123.45,
                                float = 123.45f,
                                long = 1234567890123456789L,
                                ulong = 1234567890123456789uL,
                                byte = 127,
                                ubyte = 255u,
                                char = 'c',
                                short = 12345,
                                ushort = 12345u,
                                bigInteger = BigInteger("1234567890123456789012345678901234567890"),
                                bigDecimal = BigDecimal("12345.67890"),
                                date = Date(1606991730000L),
                                calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                                uuid = UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            ),
                            Data(
                                string = "config string 2",
                                int = 456,
                                uint = 456u,
                                boolean = false,
                                double = 678.90,
                                float = 678.90f,
                                long = 987654321012345678L,
                                ulong = 987654321012345678uL,
                                byte = 126,
                                ubyte = 254u,
                                char = 'd',
                                short = 23456,
                                ushort = 23456u,
                                bigInteger = BigInteger("9876543210123456789012345678901234567890"),
                                bigDecimal = BigDecimal("67890.12345"),
                                date = Date(1609459200000L),
                                calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                                uuid = UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                            ),
                        ),
                ),
            ) shouldBe
                """
                data:
                - string: config string 1
                  int: 123
                  uint: 123
                  boolean: true
                  double: 123.45
                  float: 123.45
                  long: 1234567890123456789
                  ulong: 1234567890123456789
                  byte: 127
                  ubyte: 255
                  char: c
                  short: 12345
                  ushort: 12345
                  bigInteger: 1234567890123456789012345678901234567890
                  bigDecimal: '12345.67890'
                  date: 2020-12-03T10:35:30Z
                  calendar: 2020-12-03T10:35:30Z
                  uuid: 123e4567-e89b-12d3-a456-426655440000
                - string: config string 2
                  int: 456
                  uint: 456
                  boolean: false
                  double: 678.9
                  float: 678.9
                  long: 987654321012345678
                  ulong: 987654321012345678
                  byte: 126
                  ubyte: 254
                  char: d
                  short: 23456
                  ushort: 23456
                  bigInteger: 9876543210123456789012345678901234567890
                  bigDecimal: '67890.12345'
                  date: 2021-01-01T00:00:00Z
                  calendar: 2021-01-01T00:00:00Z
                  uuid: 123e4567-e89b-12d3-a456-426655440001
                
                """.trimIndent()
        }
    })
