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
class MapValueTest :
    FunSpec({
        data class Config(
            val string: Map<String, String>,
            val int: Map<String, Int>,
            val uint: Map<String, UInt>,
            val boolean: Map<String, Boolean>,
            val double: Map<String, Double>,
            val float: Map<String, Float>,
            val long: Map<String, Long>,
            val ulong: Map<String, ULong>,
            val byte: Map<String, Byte>,
            val ubyte: Map<String, UByte>,
            val char: Map<String, Char>,
            val short: Map<String, Short>,
            val ushort: Map<String, UShort>,
            val bigInteger: Map<String, BigInteger>,
            val bigDecimal: Map<String, BigDecimal>,
            val date: Map<String, Date>,
            val calendar: Map<String, Calendar>,
            val uuid: Map<String, UUID>,
        )

        test("should get all map values from config") {
            ktConfigString<Config>(
                """
                string:
                  key1: "config string"
                  key2: "another string"
                int:
                  key1: 123
                  key2: 456
                uint:
                  key1: 123
                  key2: 456
                boolean:
                  key1: true
                  key2: false
                double:
                  key1: 123.45
                  key2: 678.9
                float:
                  key1: 123.45
                  key2: 678.9
                long:
                  key1: 1234567890123456789
                  key2: -1234567890123456789
                ulong:
                  key1: 1234567890123456789
                  key2: 9876543210
                byte:
                  key1: 127
                  key2: 126
                ubyte:
                  key1: 255
                  key2: 254
                char:
                  key1: c
                  key2: d
                short:
                  key1: 12345
                  key2: 23456
                ushort:
                  key1: 12345
                  key2: 23456
                bigInteger:
                  key1: 1234567890123456789012345678901234567890
                  key2: 9876543210123456789012345678901234567890
                bigDecimal:
                  key1: '12345.67890'
                  key2: '67890.12345'
                date:
                  key1: 2020-12-03T10:35:30Z
                  key2: 2021-01-01T00:00:00Z
                calendar:
                  key1: 2020-12-03T10:35:30Z
                  key2: 2021-01-01T00:00:00Z
                uuid:
                  key1: 123e4567-e89b-12d3-a456-426655440000
                  key2: 123e4567-e89b-12d3-a456-426655440001
                """.trimIndent(),
            ) shouldBe
                Config(
                    string = mapOf("key1" to "config string", "key2" to "another string"),
                    int = mapOf("key1" to 123, "key2" to 456),
                    uint = mapOf("key1" to 123u, "key2" to 456u),
                    boolean = mapOf("key1" to true, "key2" to false),
                    double = mapOf("key1" to 123.45, "key2" to 678.90),
                    float = mapOf("key1" to 123.45f, "key2" to 678.90f),
                    long = mapOf("key1" to 1234567890123456789L, "key2" to -1234567890123456789L),
                    ulong = mapOf("key1" to 1234567890123456789uL, "key2" to 9876543210uL),
                    byte = mapOf("key1" to 127, "key2" to 126),
                    ubyte = mapOf("key1" to 255u, "key2" to 254u),
                    char = mapOf("key1" to 'c', "key2" to 'd'),
                    short = mapOf("key1" to 12345, "key2" to 23456),
                    ushort = mapOf("key1" to 12345u, "key2" to 23456u),
                    bigInteger =
                        mapOf(
                            "key1" to BigInteger("1234567890123456789012345678901234567890"),
                            "key2" to BigInteger("9876543210123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        mapOf(
                            "key1" to BigDecimal("12345.67890"),
                            "key2" to BigDecimal("67890.12345"),
                        ),
                    date =
                        mapOf(
                            "key1" to Date(1606991730000L),
                            "key2" to Date(1609459200000L),
                        ),
                    calendar =
                        mapOf(
                            "key1" to Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                            "key2" to Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                        ),
                    uuid =
                        mapOf(
                            "key1" to UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            "key2" to UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                        ),
                )
        }

        test("should save all map values to config") {
            saveKtConfigString(
                Config(
                    string = mapOf("key1" to "config string", "key2" to "another string"),
                    int = mapOf("key1" to 123, "key2" to 456),
                    uint = mapOf("key1" to 123u, "key2" to 456u),
                    boolean = mapOf("key1" to true, "key2" to false),
                    double = mapOf("key1" to 123.45, "key2" to 678.90),
                    float = mapOf("key1" to 123.45f, "key2" to 678.90f),
                    long = mapOf("key1" to 1234567890123456789L, "key2" to -1234567890123456789L),
                    ulong = mapOf("key1" to 1234567890123456789uL, "key2" to 9876543210uL),
                    byte = mapOf("key1" to 127, "key2" to 126),
                    ubyte = mapOf("key1" to 255u, "key2" to 254u),
                    char = mapOf("key1" to 'c', "key2" to 'd'),
                    short = mapOf("key1" to 12345, "key2" to 23456),
                    ushort = mapOf("key1" to 12345u, "key2" to 23456u),
                    bigInteger =
                        mapOf(
                            "key1" to BigInteger("1234567890123456789012345678901234567890"),
                            "key2" to BigInteger("9876543210123456789012345678901234567890"),
                        ),
                    bigDecimal =
                        mapOf(
                            "key1" to BigDecimal("12345.67890"),
                            "key2" to BigDecimal("67890.12345"),
                        ),
                    date =
                        mapOf(
                            "key1" to Date(1606991730000L),
                            "key2" to Date(1609459200000L),
                        ),
                    calendar =
                        mapOf(
                            "key1" to Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1606991730000L) },
                            "key2" to Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) },
                        ),
                    uuid =
                        mapOf(
                            "key1" to UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
                            "key2" to UUID.fromString("123e4567-e89b-12d3-a456-426655440001"),
                        ),
                ),
            ) shouldBe
                """
                string:
                  key1: config string
                  key2: another string
                int:
                  key1: 123
                  key2: 456
                uint:
                  key1: 123
                  key2: 456
                boolean:
                  key1: true
                  key2: false
                double:
                  key1: 123.45
                  key2: 678.9
                float:
                  key1: 123.45
                  key2: 678.9
                long:
                  key1: 1234567890123456789
                  key2: -1234567890123456789
                ulong:
                  key1: 1234567890123456789
                  key2: 9876543210
                byte:
                  key1: 127
                  key2: 126
                ubyte:
                  key1: 255
                  key2: 254
                char:
                  key1: c
                  key2: d
                short:
                  key1: 12345
                  key2: 23456
                ushort:
                  key1: 12345
                  key2: 23456
                bigInteger:
                  key1: 1234567890123456789012345678901234567890
                  key2: 9876543210123456789012345678901234567890
                bigDecimal:
                  key1: '12345.67890'
                  key2: '67890.12345'
                date:
                  key1: 2020-12-03T10:35:30Z
                  key2: 2021-01-01T00:00:00Z
                calendar:
                  key1: 2020-12-03T10:35:30Z
                  key2: 2021-01-01T00:00:00Z
                uuid:
                  key1: 123e4567-e89b-12d3-a456-426655440000
                  key2: 123e4567-e89b-12d3-a456-426655440001
                
                """.trimIndent()
        }
    })
