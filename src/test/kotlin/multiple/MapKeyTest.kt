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
class MapKeyTest :
    FunSpec({
        data class Config(
            val string: Map<String, Int>,
            val int: Map<Int, String>,
            val uint: Map<UInt, String>,
            val boolean: Map<Boolean, String>,
            val double: Map<Double, String>,
            val float: Map<Float, String>,
            val long: Map<Long, String>,
            val ulong: Map<ULong, String>,
            val byte: Map<Byte, String>,
            val ubyte: Map<UByte, String>,
            val char: Map<Char, String>,
            val short: Map<Short, String>,
            val ushort: Map<UShort, String>,
            val bigInteger: Map<BigInteger, String>,
            val bigDecimal: Map<BigDecimal, String>,
            val date: Map<Date, String>,
            val calendar: Map<Calendar, String>,
            val uuid: Map<UUID, String>,
        )

        test("should get all map values from config") {
            ktConfigString<Config>(
                """
                string:
                  "config string": 1
                  "another string": 2
                int:
                  123: "string for int key"
                  456: "another string for int key"
                uint:
                  123: "string for uint key"
                  456: "another string for uint key"
                boolean:
                  true: "string for true"
                  false: "string for false"
                double:
                  123.45: "string for double"
                  678.90: "another string for double"
                float:
                  123.45: "string for float"
                  678.90: "another string for float"
                long:
                  1234567890123456789: "string for long"
                  -1234567890123456789: "another string for long"
                ulong:
                  1234567890123456789: "string for ulong"
                  9876543210: "another string for ulong"
                byte:
                  127: "string for byte"
                  126: "another string for byte"
                ubyte:
                  255: "string for ubyte"
                  254: "another string for ubyte"
                char:
                  c: "string for char"
                  d: "another string for char"
                short:
                  12345: "string for short"
                  23456: "another string for short"
                ushort:
                  12345: "string for ushort"
                  23456: "another string for ushort"
                bigInteger:
                  1234567890123456789012345678901234567890: "string for bigInteger"
                  9876543210123456789012345678901234567890: "another string for bigInteger"
                bigDecimal:
                  12345.6789: "string for bigDecimal"
                  67890.12345: "another string for bigDecimal"
                date:
                  "2020-12-03T10:35:30Z": "string for date"
                  "2021-01-01T00:00:00Z": "another string for date"
                calendar:
                  "2020-12-03T10:35:30Z": "string for calendar"
                  "2021-01-01T00:00:00Z": "another string for calendar"
                uuid:
                  123e4567-e89b-12d3-a456-426655440000: "string for uuid"
                  123e4567-e89b-12d3-a456-426655440001: "another string for uuid"
                """.trimIndent(),
            ) shouldBe
                Config(
                    string = mapOf("config string" to 1, "another string" to 2),
                    int = mapOf(123 to "string for int key", 456 to "another string for int key"),
                    uint = mapOf(123u to "string for uint key", 456u to "another string for uint key"),
                    boolean = mapOf(true to "string for true", false to "string for false"),
                    double = mapOf(123.45 to "string for double", 678.90 to "another string for double"),
                    float = mapOf(123.45f to "string for float", 678.90f to "another string for float"),
                    long = mapOf(1234567890123456789L to "string for long", -1234567890123456789L to "another string for long"),
                    ulong = mapOf(1234567890123456789uL to "string for ulong", 9876543210uL to "another string for ulong"),
                    byte = mapOf(127.toByte() to "string for byte", 126.toByte() to "another string for byte"),
                    ubyte = mapOf(255.toUByte() to "string for ubyte", 254.toUByte() to "another string for ubyte"),
                    char = mapOf('c' to "string for char", 'd' to "another string for char"),
                    short = mapOf(12345.toShort() to "string for short", 23456.toShort() to "another string for short"),
                    ushort = mapOf(12345.toUShort() to "string for ushort", 23456.toUShort() to "another string for ushort"),
                    bigInteger =
                        mapOf(
                            BigInteger("1234567890123456789012345678901234567890") to "string for bigInteger",
                            BigInteger("9876543210123456789012345678901234567890") to "another string for bigInteger",
                        ),
                    bigDecimal =
                        mapOf(
                            BigDecimal("12345.6789") to "string for bigDecimal",
                            BigDecimal("67890.12345") to "another string for bigDecimal",
                        ),
                    date =
                        mapOf(
                            Date(1606991730000L) to "string for date",
                            Date(1609459200000L) to "another string for date",
                        ),
                    calendar =
                        mapOf(
                            Calendar
                                .getInstance(
                                    TimeZone.getTimeZone("UTC"),
                                ).apply { time = Date(1606991730000L) } to "string for calendar",
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) } to
                                "another string for calendar",
                        ),
                    uuid =
                        mapOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000") to "string for uuid",
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440001") to "another string for uuid",
                        ),
                )
        }

        test("should save all map values to config") {
            saveKtConfigString(
                Config(
                    string = mapOf("config string" to 1, "another string" to 2),
                    int = mapOf(123 to "string for int key", 456 to "another string for int key"),
                    uint = mapOf(123u to "string for uint key", 456u to "another string for uint key"),
                    boolean = mapOf(true to "string for true", false to "string for false"),
                    double = mapOf(123.45 to "string for double", 678.90 to "another string for double"),
                    float = mapOf(123.45f to "string for float", 678.90f to "another string for float"),
                    long = mapOf(1234567890123456789L to "string for long", -1234567890123456789L to "another string for long"),
                    ulong = mapOf(1234567890123456789uL to "string for ulong", 9876543210uL to "another string for ulong"),
                    byte = mapOf(127.toByte() to "string for byte", 126.toByte() to "another string for byte"),
                    ubyte = mapOf(255.toUByte() to "string for ubyte", 254.toUByte() to "another string for ubyte"),
                    char = mapOf('c' to "string for char", 'd' to "another string for char"),
                    short = mapOf(12345.toShort() to "string for short", 23456.toShort() to "another string for short"),
                    ushort = mapOf(12345.toUShort() to "string for ushort", 23456.toUShort() to "another string for ushort"),
                    bigInteger =
                        mapOf(
                            BigInteger("1234567890123456789012345678901234567890") to "string for bigInteger",
                            BigInteger("9876543210123456789012345678901234567890") to "another string for bigInteger",
                        ),
                    bigDecimal =
                        mapOf(
                            BigDecimal("12345.67890") to "string for bigDecimal",
                            BigDecimal("67890.12345") to "another string for bigDecimal",
                        ),
                    date =
                        mapOf(
                            Date(1606991730000L) to "string for date",
                            Date(1609459200000L) to "another string for date",
                        ),
                    calendar =
                        mapOf(
                            Calendar
                                .getInstance(
                                    TimeZone.getTimeZone("UTC"),
                                ).apply { time = Date(1606991730000L) } to "string for calendar",
                            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = Date(1609459200000L) } to
                                "another string for calendar",
                        ),
                    uuid =
                        mapOf(
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440000") to "string for uuid",
                            UUID.fromString("123e4567-e89b-12d3-a456-426655440001") to "another string for uuid",
                        ),
                ),
            ) shouldBe
                """
                string:
                  config string: 1
                  another string: 2
                int:
                  '123': string for int key
                  '456': another string for int key
                uint:
                  '123': string for uint key
                  '456': another string for uint key
                boolean:
                  'true': string for true
                  'false': string for false
                double:
                  '123.45': string for double
                  '678.9': another string for double
                float:
                  '123.45': string for float
                  '678.9': another string for float
                long:
                  '1234567890123456789': string for long
                  '-1234567890123456789': another string for long
                ulong:
                  '1234567890123456789': string for ulong
                  '9876543210': another string for ulong
                byte:
                  '127': string for byte
                  '126': another string for byte
                ubyte:
                  '255': string for ubyte
                  '254': another string for ubyte
                char:
                  c: string for char
                  d: another string for char
                short:
                  '12345': string for short
                  '23456': another string for short
                ushort:
                  '12345': string for ushort
                  '23456': another string for ushort
                bigInteger:
                  '1234567890123456789012345678901234567890': string for bigInteger
                  '9876543210123456789012345678901234567890': another string for bigInteger
                bigDecimal:
                  '12345.67890': string for bigDecimal
                  '67890.12345': another string for bigDecimal
                date:
                  '2020-12-03T10:35:30Z': string for date
                  '2021-01-01T00:00:00Z': another string for date
                calendar:
                  '2020-12-03T10:35:30Z': string for calendar
                  '2021-01-01T00:00:00Z': another string for calendar
                uuid:
                  123e4567-e89b-12d3-a456-426655440000: string for uuid
                  123e4567-e89b-12d3-a456-426655440001: another string for uuid
                
                """.trimIndent()
        }
    })
