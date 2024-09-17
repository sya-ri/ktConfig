package multiple

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID

@Suppress("unused")
class ListTest :
    FunSpec({
        data class Config(
            val string: List<String>,
            val int: List<Int>,
            val uint: List<UInt>,
            val boolean: List<Boolean>,
            val double: List<Double>,
            val float: List<Float>,
            val long: List<Long>,
            val ulong: List<ULong>,
            val byte: List<Byte>,
            val ubyte: List<UByte>,
            val char: List<Char>,
            val short: List<Short>,
            val ushort: List<UShort>,
            val bigInteger: List<BigInteger>,
            val bigDecimal: List<BigDecimal>,
            val date: List<Date>,
            val calendar: List<Calendar>,
            val uuid: List<UUID>,
        )

        test("should get single values as list") {
            ktConfigString<Config>(
                """
                string: config string
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
                """.trimIndent(),
            ) shouldBe
                Config(
                    string = listOf("config string"),
                    int = listOf(123),
                    uint = listOf(123u),
                    boolean = listOf(true),
                    double = listOf(123.45),
                    float = listOf(123.45f),
                    long = listOf(1234567890123456789L),
                    ulong = listOf(1234567890123456789uL),
                    byte = listOf(127),
                    ubyte = listOf(255u),
                    char = listOf('c'),
                    short = listOf(12345),
                    ushort = listOf(12345u),
                    bigInteger =
                        listOf(
                            BigInteger("1234567890123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        listOf(
                            BigDecimal("12345.67890"),
                        ),
                    date =
                        listOf(
                            Date(1606991730000L),
                        ),
                    calendar =
                        listOf(
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                        ),
                    uuid =
                        listOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                        ),
                )
        }

        context("should get all list values from config") {
            withData(
                """
                string: [config string, another string]
                int: [123, 456]
                uint: [123, 456]
                boolean: [true, false]
                double: [123.45, 678.90]
                float: [123.45, 678.90]
                long: [1234567890123456789, -1234567890123456789]
                ulong: [1234567890123456789, 9876543210]
                byte: [127, 126]
                ubyte: [255, 254]
                char: [c, d]
                short: [12345, 23456]
                ushort: [12345, 23456]
                bigInteger: [1234567890123456789012345678901234567890, 9876543210123456789012345678901234567890]
                bigDecimal: ['12345.67890', '67890.12345']
                date: [2020-12-03T10:35:30Z, 2021-01-01T00:00:00Z]
                calendar: [2020-12-03T10:35:30Z, 2021-01-01T00:00:00Z]
                uuid: [123e4567-e89b-12d3-a456-426655440000, 123e4567-e89b-12d3-a456-426655440001]
                """.trimIndent(),
                """
                string:
                - config string
                - another string
                int:
                - 123
                - 456
                uint:
                - 123
                - 456
                boolean:
                - true
                - false
                double:
                - 123.45
                - 678.9
                float:
                - 123.45
                - 678.9
                long:
                - 1234567890123456789
                - -1234567890123456789
                ulong:
                - 1234567890123456789
                - 9876543210
                byte:
                - 127
                - 126
                ubyte:
                - 255
                - 254
                char:
                - c
                - d
                short:
                - 12345
                - 23456
                ushort:
                - 12345
                - 23456
                bigInteger:
                - 1234567890123456789012345678901234567890
                - 9876543210123456789012345678901234567890
                bigDecimal:
                - '12345.67890'
                - '67890.12345'
                date:
                - 2020-12-03T10:35:30Z
                - 2021-01-01T00:00:00Z
                calendar:
                - 2020-12-03T10:35:30Z
                - 2021-01-01T00:00:00Z
                uuid:
                - 123e4567-e89b-12d3-a456-426655440000
                - 123e4567-e89b-12d3-a456-426655440001
                """.trimIndent(),
            ) { yaml ->
                ktConfigString<Config>(
                    yaml,
                ) shouldBe
                    Config(
                        string = listOf("config string", "another string"),
                        int = listOf(123, 456),
                        uint = listOf(123u, 456u),
                        boolean = listOf(true, false),
                        double = listOf(123.45, 678.90),
                        float = listOf(123.45f, 678.90f),
                        long = listOf(1234567890123456789L, -1234567890123456789L),
                        ulong = listOf(1234567890123456789uL, 9876543210uL),
                        byte = listOf(127, 126),
                        ubyte = listOf(255u, 254u),
                        char = listOf('c', 'd'),
                        short = listOf(12345, 23456),
                        ushort = listOf(12345u, 23456u),
                        bigInteger =
                            listOf(
                                BigInteger("1234567890123456789012345678901234567890"),
                                BigInteger("9876543210123456789012345678901234567890"),
                            ),
                        bigDecimal =
                            listOf(
                                BigDecimal("12345.67890"),
                                BigDecimal("67890.12345"),
                            ),
                        date =
                            listOf(
                                Date(1606991730000L),
                                Date(1609459200000L),
                            ),
                        calendar =
                            listOf(
                                Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                                Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                            ),
                        uuid =
                            listOf(
                                UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                                UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                            ),
                    )
            }
        }

        test("should save all list values to config") {
            saveKtConfigString(
                Config(
                    string = listOf("config string", "another string"),
                    int = listOf(123, 456),
                    uint = listOf(123u, 456u),
                    boolean = listOf(true, false),
                    double = listOf(123.45, 678.90),
                    float = listOf(123.45f, 678.90f),
                    long = listOf(1234567890123456789L, -1234567890123456789L),
                    ulong = listOf(1234567890123456789uL, 9876543210uL),
                    byte = listOf(127, 126),
                    ubyte = listOf(255u, 254u),
                    char = listOf('c', 'd'),
                    short = listOf(12345, 23456),
                    ushort = listOf(12345u, 23456u),
                    bigInteger =
                        listOf(
                            BigInteger("1234567890123456789012345678901234567890"),
                            BigInteger("9876543210123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        listOf(
                            BigDecimal("12345.67890"),
                            BigDecimal("67890.12345"),
                        ),
                    date =
                        listOf(
                            Date(1606991730000L),
                            Date(1609459200000L),
                        ),
                    calendar =
                        listOf(
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                        ),
                    uuid =
                        listOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                        ),
                ),
            ) shouldBe
                """
                string:
                - config string
                - another string
                int:
                - 123
                - 456
                uint:
                - 123
                - 456
                boolean:
                - true
                - false
                double:
                - 123.45
                - 678.9
                float:
                - 123.45
                - 678.9
                long:
                - 1234567890123456789
                - -1234567890123456789
                ulong:
                - 1234567890123456789
                - 9876543210
                byte:
                - 127
                - 126
                ubyte:
                - 255
                - 254
                char:
                - c
                - d
                short:
                - 12345
                - 23456
                ushort:
                - 12345
                - 23456
                bigInteger:
                - 1234567890123456789012345678901234567890
                - 9876543210123456789012345678901234567890
                bigDecimal:
                - '12345.67890'
                - '67890.12345'
                date:
                - 2020-12-03T10:35:30Z
                - 2021-01-01T00:00:00Z
                calendar:
                - 2020-12-03T10:35:30Z
                - 2021-01-01T00:00:00Z
                uuid:
                - 123e4567-e89b-12d3-a456-426655440000
                - 123e4567-e89b-12d3-a456-426655440001
                
                """.trimIndent()
        }

        test("should get all duplicate list values from config") {
            val yaml =
                """
                string: [duplicate string, duplicate string]
                int: [123, 123]
                uint: [123, 123]
                boolean: [true, true]
                double: [123.45, 123.45]
                float: [123.45, 123.45]
                long: [1234567890123456789, 1234567890123456789]
                ulong: [1234567890123456789, 1234567890123456789]
                byte: [127, 127]
                ubyte: [255, 255]
                char: [c, c]
                short: [12345, 12345]
                ushort: [12345, 12345]
                bigInteger: [1234567890123456789012345678901234567890, 1234567890123456789012345678901234567890]
                bigDecimal: ['12345.67890', '12345.67890']
                date: [2020-12-03T10:35:30Z, 2020-12-03T10:35:30Z]
                calendar: [2020-12-03T10:35:30Z, 2020-12-03T10:35:30Z]
                uuid: [123e4567-e89b-12d3-a456-426655440000, 123e4567-e89b-12d3-a456-426655440000]
                """.trimIndent()
            ktConfigString<Config>(yaml) shouldBe
                Config(
                    string = listOf("duplicate string", "duplicate string"),
                    int = listOf(123, 123),
                    uint = listOf(123u, 123u),
                    boolean = listOf(true, true),
                    double = listOf(123.45, 123.45),
                    float = listOf(123.45f, 123.45f),
                    long = listOf(1234567890123456789L, 1234567890123456789L),
                    ulong = listOf(1234567890123456789uL, 1234567890123456789uL),
                    byte = listOf(127, 127),
                    ubyte = listOf(255u, 255u),
                    char = listOf('c', 'c'),
                    short = listOf(12345, 12345),
                    ushort = listOf(12345u, 12345u),
                    bigInteger =
                        listOf(
                            BigInteger("1234567890123456789012345678901234567890"),
                            BigInteger("1234567890123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        listOf(
                            BigDecimal("12345.67890"),
                            BigDecimal("12345.67890"),
                        ),
                    date =
                        listOf(
                            Date(1606991730000L),
                            Date(1606991730000L),
                        ),
                    calendar =
                        listOf(
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                        ),
                    uuid =
                        listOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                        ),
                )
        }

        test("should save all duplicate list values to config") {
            val config =
                Config(
                    string = listOf("duplicate string", "duplicate string"),
                    int = listOf(123, 123),
                    uint = listOf(123u, 123u),
                    boolean = listOf(true, true),
                    double = listOf(123.45, 123.45),
                    float = listOf(123.45f, 123.45f),
                    long = listOf(1234567890123456789L, 1234567890123456789L),
                    ulong = listOf(1234567890123456789uL, 1234567890123456789uL),
                    byte = listOf(127, 127),
                    ubyte = listOf(255u, 255u),
                    char = listOf('c', 'c'),
                    short = listOf(12345, 12345),
                    ushort = listOf(12345u, 12345u),
                    bigInteger =
                        listOf(
                            BigInteger("1234567890123456789012345678901234567890"),
                            BigInteger("1234567890123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        listOf(
                            BigDecimal("12345.67890"),
                            BigDecimal("12345.67890"),
                        ),
                    date =
                        listOf(
                            Date(1606991730000L),
                            Date(1606991730000L),
                        ),
                    calendar =
                        listOf(
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                        ),
                    uuid =
                        listOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                        ),
                )
            saveKtConfigString(config) shouldBe
                """
                string:
                - duplicate string
                - duplicate string
                int:
                - 123
                - 123
                uint:
                - 123
                - 123
                boolean:
                - true
                - true
                double:
                - 123.45
                - 123.45
                float:
                - 123.45
                - 123.45
                long:
                - 1234567890123456789
                - 1234567890123456789
                ulong:
                - 1234567890123456789
                - 1234567890123456789
                byte:
                - 127
                - 127
                ubyte:
                - 255
                - 255
                char:
                - c
                - c
                short:
                - 12345
                - 12345
                ushort:
                - 12345
                - 12345
                bigInteger:
                - 1234567890123456789012345678901234567890
                - 1234567890123456789012345678901234567890
                bigDecimal:
                - '12345.67890'
                - '12345.67890'
                date:
                - 2020-12-03T10:35:30Z
                - 2020-12-03T10:35:30Z
                calendar:
                - 2020-12-03T10:35:30Z
                - 2020-12-03T10:35:30Z
                uuid:
                - 123e4567-e89b-12d3-a456-426655440000
                - 123e4567-e89b-12d3-a456-426655440000
                
                """.trimIndent()
        }
    })
