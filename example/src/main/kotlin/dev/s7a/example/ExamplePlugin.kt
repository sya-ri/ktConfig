package dev.s7a.example

import dev.s7a.example.config.HasDefaultConfigLoader
import dev.s7a.example.config.SealedTestConfig
import dev.s7a.example.config.SealedTestConfigBLoader
import dev.s7a.example.config.SealedTestConfigLoader
import dev.s7a.example.config.SerializerTestConfig
import dev.s7a.example.config.SerializerTestConfigLoader
import dev.s7a.example.type.CustomData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

@Suppress("unused")
class ExamplePlugin : JavaPlugin() {
    private val output = Output(this)

    override fun onEnable() {
        testSerializer()
        testDefaultSerializer()
        testSealedSerializer()
        server.shutdown()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun testSerializer() {
        val data =
            SerializerTestConfig(
                string = UUID.randomUUID().toString(),
                byte = Random.nextBytes(1)[0],
                char = Random.nextInt(32, 127).toChar(),
                int = Random.nextInt(),
                long = Random.nextLong(),
                short = Random.nextInt(-32768, 32767).toShort(),
                double = Random.nextDouble(),
                float = Random.nextFloat(),
                uByte = Random.nextInt(0, 255).toUByte(),
                uInt = Random.nextInt().toUInt(),
                uLong = Random.nextLong().toULong(),
                uShort = Random.nextInt(0, 65535).toUShort(),
                boolean = Random.nextBoolean(),
                uuid = UUID.randomUUID(),
                localTime = LocalTime.now(),
                localDate = LocalDate.now(),
                localDateTime = LocalDateTime.now(),
                year = Year.now(),
                yearMonth = YearMonth.now(),
                offsetTime = OffsetTime.now(),
                offsetDateTime = OffsetDateTime.now(),
                zonedDateTime = ZonedDateTime.now(),
                instant = Instant.now(),
                duration = Duration.ofSeconds(Random.nextLong()),
                period = Period.of(Random.nextInt(), Random.nextInt(), Random.nextInt()),
                itemStack = ItemStack(Material.STONE, Random.nextInt(1, 64)),
                location =
                    Location(
                        Bukkit.getWorlds().random(),
                        Random.nextDouble(),
                        Random.nextDouble(),
                        Random.nextDouble(),
                        Random.nextFloat(),
                        Random.nextFloat(),
                    ),
                formattedVector = Vector(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()),
                formattedVector2 = Vector(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()),
                formattedVector3 = Vector(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()),
                formattedVector4 = Vector(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()),
                customData = CustomData(Random.nextInt()),
                array = Array(3) { UUID.randomUUID().toString() },
                byteArray = ByteArray(3) { Random.nextInt(-128, 128).toByte() },
                charArray = CharArray(3) { Random.nextInt(32, 127).toChar() },
                intArray = IntArray(3) { Random.nextInt() },
                longArray = LongArray(3) { Random.nextLong() },
                shortArray = ShortArray(3) { Random.nextInt(-32768, 32767).toShort() },
                doubleArray = DoubleArray(3) { Random.nextDouble() },
                floatArray = FloatArray(3) { Random.nextFloat() },
                uByteArray = UByteArray(3) { Random.nextInt(0, 255).toUByte() },
                uIntArray = UIntArray(3) { Random.nextInt().toUInt() },
                uLongArray = ULongArray(3) { Random.nextLong().toULong() },
                uShortArray = UShortArray(3) { Random.nextInt(0, 65535).toUShort() },
                booleanArray = BooleanArray(3) { Random.nextBoolean() },
                list = List(5) { Random.nextInt().toUInt() },
                list2 = List(5) { List(3) { Random.nextInt().toUInt() } },
                set = List(5) { Random.nextInt().toUInt() }.toSet(),
                arrayDeque = ArrayDeque(List(3) { Random.nextInt().toUInt() }),
                map = (0..4).associate { Random.nextLong().toULong() to Random.nextLong().toULong() },
                map2 =
                    (0..4).associate {
                        Random.nextInt().toUInt() to
                            (0..2).associate {
                                Random.nextInt().toUInt() to Random.nextInt().toUInt()
                            }
                    },
                listMap = List(5) { (0..2).associate { Random.nextInt().toUInt() to Random.nextInt().toUInt() } },
                mapList = (0..4).associate { Random.nextInt().toUInt() to List(3) { Random.nextInt().toUInt() } },
                enum = SerializerTestConfig.TestEnum.entries.random(),
                enumList = List(3) { SerializerTestConfig.TestEnum.entries.random() },
                enumMap = SerializerTestConfig.TestEnum.entries.associateWith { SerializerTestConfig.TestEnum.entries.random() },
                value = SerializerTestConfig.Value(UUID.randomUUID().toString()),
                valueList =
                    SerializerTestConfig.ValueList(
                        List(3) {
                            SerializerTestConfig.Value(
                                UUID.randomUUID().toString(),
                            )
                        },
                    ),
                valueMap =
                    SerializerTestConfig.ValueMap(
                        (0..2).associate {
                            SerializerTestConfig.Value(UUID.randomUUID().toString()) to
                                SerializerTestConfig.UIntValue(Random.nextInt().toUInt())
                        },
                    ),
                nested =
                    SerializerTestConfig.Nested(
                        UUID.randomUUID().toString(),
                        Random.nextInt().toUInt(),
                        SerializerTestConfig.Nested(
                            UUID.randomUUID().toString(),
                            Random.nextInt().toUInt(),
                            null,
                        ),
                    ),
                nestedList =
                    listOf(
                        SerializerTestConfig.Nested(
                            UUID.randomUUID().toString(),
                            Random.nextInt().toUInt(),
                            null,
                        ),
                        SerializerTestConfig.Nested(
                            UUID.randomUUID().toString(),
                            Random.nextInt().toUInt(),
                            SerializerTestConfig.Nested(
                                UUID.randomUUID().toString(),
                                Random.nextInt().toUInt(),
                                null,
                            ),
                        ),
                    ),
                nestedMap =
                    mapOf(
                        "map1" to
                            SerializerTestConfig.Nested(
                                UUID.randomUUID().toString(),
                                Random.nextInt().toUInt(),
                                null,
                            ),
                        "map2" to
                            SerializerTestConfig.Nested(
                                UUID.randomUUID().toString(),
                                Random.nextInt().toUInt(),
                                SerializerTestConfig.Nested(
                                    UUID.randomUUID().toString(),
                                    Random.nextInt().toUInt(),
                                    null,
                                ),
                            ),
                    ),
                nullable = null,
                nullableList = listOf(null, UUID.randomUUID().toString(), null),
                nullableArray = arrayOf(null, UUID.randomUUID().toString(), null),
                nullableArrayDeque = ArrayDeque(listOf(null, UUID.randomUUID().toString(), null)),
                nullableSet = setOf(null, UUID.randomUUID().toString(), null),
                nullableMap = mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                nullableMap2 =
                    mapOf(
                        "map1" to mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                        "map2" to mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                    ),
                nullableListMap =
                    listOf(
                        mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                        mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                    ),
                nullableMapList =
                    mapOf(
                        "null" to listOf(null, UUID.randomUUID().toString(), null),
                        "not null" to listOf(null, UUID.randomUUID().toString(), null),
                    ),
                nullableListNullableMap =
                    listOf(
                        mapOf("null" to null, "not null" to UUID.randomUUID().toString()),
                    ),
                pathName = UUID.randomUUID().toString(),
                listPathName =
                    listOf(
                        SerializerTestConfig.NestedPathName(UUID.randomUUID().toString()),
                    ),
                mapPathName =
                    mapOf(
                        "key1" to SerializerTestConfig.NestedPathName(UUID.randomUUID().toString()),
                    ),
            )

        val expected =
            data.copy(
                // Null values in maps are ignored during serialization
                nullableMap = data.nullableMap.filterValues { it != null },
                nullableMap2 = data.nullableMap2.mapValues { map -> map.value.filterValues { it != null } },
            )

        output.info("Save SerializerTestConfig:")
        output.info(data.toString())
        output.info()

        val yaml = SerializerTestConfigLoader.saveToString(data)

        output.info("SerializerTestConfig:")
        output.info(yaml)
        output.info()

        output.info("Check comments:")
        val lines = yaml.lines()
        if (lines[0] == "# Header comment" && lines[1] == "# Second line in header") {
            output.info("- Header comment found")
        } else {
            output.error("Header comment not found")
        }
        if (lines[3] == "# Property comment" && lines[4] == "# Second line in property") {
            output.info("- Property comment found")
        } else {
            if (Bukkit.getBukkitVersion() == "1.8.8-R0.1-SNAPSHOT") {
                // Property comment is not supported in 1.8.8
                output.info("- Property comment not found (unsupported)")
            } else {
                output.error("Property comment not found")
            }
        }
        output.info()

        output.info("Check @PathName")
        if (lines.contains("path-name: ${expected.pathName}")) {
            output.info("- Path name is overridden")
        } else {
            output.error("Path name not found")
        }
        if (lines.contains("- path-name: ${expected.listPathName[0].string}")) {
            output.info("- List path name is overridden")
        } else {
            output.error("List path name not found")
        }
        if (lines.contains("    path-name: ${expected.mapPathName["key1"]!!.string}")) {
            output.info("- Map path name is overridden")
        } else {
            output.error("Map path name not found")
        }
        output.info()

        val actual = SerializerTestConfigLoader.loadFromString(yaml)

        output.info("Loaded SerializerTestConfig:")
        output.info(actual.toString())

        if (expected != actual) {
            output.error("Differences found:")
            if (expected.string != actual.string) {
                output.error("string: expected=${expected.string}, actual=${actual.string}")
            }
            if (expected.byte != actual.byte) {
                output.error("byte: expected=${expected.byte}, actual=${actual.byte}")
            }
            if (expected.char != actual.char) {
                output.error("char: expected=${expected.char}, actual=${actual.char}")
            }
            if (expected.int != actual.int) {
                output.error("int: expected=${expected.int}, actual=${actual.int}")
            }
            if (expected.long != actual.long) {
                output.error("long: expected=${expected.long}, actual=${actual.long}")
            }
            if (expected.short != actual.short) {
                output.error("short: expected=${expected.short}, actual=${actual.short}")
            }
            if (expected.double != actual.double) {
                output.error("double: expected=${expected.double}, actual=${actual.double}")
            }
            if (expected.float != actual.float) {
                output.error("float: expected=${expected.float}, actual=${actual.float}")
            }
            if (expected.uByte != actual.uByte) {
                output.error("uByte: expected=${expected.uByte}, actual=${actual.uByte}")
            }
            if (expected.uInt != actual.uInt) {
                output.error("uInt: expected=${expected.uInt}, actual=${actual.uInt}")
            }
            if (expected.uLong != actual.uLong) {
                output.error("uLong: expected=${expected.uLong}, actual=${actual.uLong}")
            }
            if (expected.uShort != actual.uShort) {
                output.error("uShort: expected=${expected.uShort}, actual=${actual.uShort}")
            }
            if (expected.boolean != actual.boolean) {
                output.error("boolean: expected=${expected.boolean}, actual=${actual.boolean}")
            }
            if (expected.uuid != actual.uuid) {
                output.error("uuid: expected=${expected.uuid}, actual=${actual.uuid}")
            }
            if (expected.localTime != actual.localTime) {
                output.error("localTime: expected=${expected.localTime}, actual=${actual.localTime}")
            }
            if (expected.localDate != actual.localDate) {
                output.error("localDate: expected=${expected.localDate}, actual=${actual.localDate}")
            }
            if (expected.localDateTime != actual.localDateTime) {
                output.error("localDateTime: expected=${expected.localDateTime}, actual=${actual.localDateTime}")
            }
            if (expected.year != actual.year) {
                output.error("year: expected=${expected.year}, actual=${actual.year}")
            }
            if (expected.yearMonth != actual.yearMonth) {
                output.error("yearMonth: expected=${expected.yearMonth}, actual=${actual.yearMonth}")
            }
            if (expected.offsetTime != actual.offsetTime) {
                output.error("offsetTime: expected=${expected.offsetTime}, actual=${actual.offsetTime}")
            }
            if (expected.offsetDateTime != actual.offsetDateTime) {
                output.error("offsetDateTime: expected=${expected.offsetDateTime}, actual=${actual.offsetDateTime}")
            }
            if (expected.zonedDateTime != actual.zonedDateTime) {
                output.error("zonedDateTime: expected=${expected.zonedDateTime}, actual=${actual.zonedDateTime}")
            }
            if (expected.instant != actual.instant) {
                output.error("instant: expected=${expected.instant}, actual=${actual.instant}")
            }
            if (expected.duration != actual.duration) {
                output.error("duration: expected=${expected.duration}, actual=${actual.duration}")
            }
            if (expected.period != actual.period) {
                output.error("period: expected=${expected.period}, actual=${actual.period}")
            }
            if (expected.itemStack != actual.itemStack) {
                output.error("itemStack: expected=${expected.itemStack}, actual=${actual.itemStack}")
            }
            if (expected.location != actual.location) {
                output.error("location: expected=${expected.location}, actual=${actual.location}")
            }
            if (expected.formattedVector != actual.formattedVector) {
                output.error(
                    "formattedVector: expected=${expected.formattedVector}, actual=${actual.formattedVector}",
                )
            }
            if (expected.formattedVector2 != actual.formattedVector2) {
                output.error(
                    "formattedVector2: expected=${expected.formattedVector2}, actual=${actual.formattedVector2}",
                )
            }
            if (expected.formattedVector3 != actual.formattedVector3) {
                output.error(
                    "formattedVector3: expected=${expected.formattedVector3}, actual=${actual.formattedVector3}",
                )
            }
            if (expected.formattedVector4 != actual.formattedVector4) {
                output.error(
                    "formattedVector4: expected=${expected.formattedVector4}, actual=${actual.formattedVector4}",
                )
            }
            if (expected.customData != actual.customData) {
                output.error(
                    "customData: expected=${expected.customData}, actual=${actual.customData}",
                )
            }
            if (expected.array.contentEquals(actual.array).not()) {
                output.error("array: expected=${expected.array.contentToString()}, actual=${actual.array.contentToString()}")
            }
            if (expected.byteArray.contentEquals(actual.byteArray).not()) {
                output.error("byteArray: expected=${expected.byteArray.contentToString()}, actual=${actual.byteArray.contentToString()}")
            }
            if (expected.charArray.contentEquals(actual.charArray).not()) {
                output.error("charArray: expected=${expected.charArray.contentToString()}, actual=${actual.charArray.contentToString()}")
            }
            if (expected.intArray.contentEquals(actual.intArray).not()) {
                output.error("intArray: expected=${expected.intArray.contentToString()}, actual=${actual.intArray.contentToString()}")
            }
            if (expected.longArray.contentEquals(actual.longArray).not()) {
                output.error("longArray: expected=${expected.longArray.contentToString()}, actual=${actual.longArray.contentToString()}")
            }
            if (expected.shortArray.contentEquals(actual.shortArray).not()) {
                output.error("shortArray: expected=${expected.shortArray.contentToString()}, actual=${actual.shortArray.contentToString()}")
            }
            if (expected.doubleArray.contentEquals(actual.doubleArray).not()) {
                output.error(
                    "doubleArray: expected=${expected.doubleArray.contentToString()}, actual=${actual.doubleArray.contentToString()}",
                )
            }
            if (expected.floatArray.contentEquals(actual.floatArray).not()) {
                output.error("floatArray: expected=${expected.floatArray.contentToString()}, actual=${actual.floatArray.contentToString()}")
            }
            if (expected.uByteArray.contentEquals(actual.uByteArray).not()) {
                output.error("uByteArray: expected=${expected.uByteArray.contentToString()}, actual=${actual.uByteArray.contentToString()}")
            }
            if (expected.uIntArray.contentEquals(actual.uIntArray).not()) {
                output.error("uIntArray: expected=${expected.uIntArray.contentToString()}, actual=${actual.uIntArray.contentToString()}")
            }
            if (expected.uLongArray.contentEquals(actual.uLongArray).not()) {
                output.error("uLongArray: expected=${expected.uLongArray.contentToString()}, actual=${actual.uLongArray.contentToString()}")
            }
            if (expected.uShortArray.contentEquals(actual.uShortArray).not()) {
                output.error(
                    "uShortArray: expected=${expected.uShortArray.contentToString()}, actual=${actual.uShortArray.contentToString()}",
                )
            }
            if (expected.booleanArray.contentEquals(actual.booleanArray).not()) {
                output.error(
                    "booleanArray: expected=${expected.booleanArray.contentToString()}, actual=${actual.booleanArray.contentToString()}",
                )
            }
            if (expected.list != actual.list) {
                output.error("list: expected=${expected.list}, actual=${actual.list}")
            }
            if (expected.list2 != actual.list2) {
                output.error("list2: expected=${expected.list2}, actual=${actual.list2}")
            }
            if (expected.set != actual.set) {
                output.error("set: expected=${expected.set}, actual=${actual.set}")
            }
            if (expected.array.contentEquals(actual.array).not()) {
                output.error("array: expected=${expected.array}, actual=${actual.array}")
            }
            if (expected.arrayDeque != actual.arrayDeque) {
                output.error("arrayDeque: expected=${expected.arrayDeque}, actual=${actual.arrayDeque}")
            }
            if (expected.map != actual.map) {
                output.error("map: expected=${expected.map}, actual=${actual.map}")
            }
            if (expected.map2 != actual.map2) {
                output.error("map2: expected=${expected.map2}, actual=${actual.map2}")
            }
            if (expected.listMap != actual.listMap) {
                output.error("listMap: expected=${expected.listMap}, actual=${actual.listMap}")
            }
            if (expected.mapList != actual.mapList) {
                output.error("mapList: expected=${expected.mapList}, actual=${actual.mapList}")
            }
            if (expected.enum != actual.enum) {
                output.error("enum: expected=${expected.enum}, actual=${actual.enum}")
            }
            if (expected.enumList != actual.enumList) {
                output.error("enumList: expected=${expected.enumList}, actual=${actual.enumList}")
            }
            if (expected.enumMap != actual.enumMap) {
                output.error("enumMap: expected=${expected.enumMap}, actual=${actual.enumMap}")
            }
            if (expected.value != actual.value) {
                output.error("value: expected=${expected.value}, actual=${actual.value}")
            }
            if (expected.valueList != actual.valueList) {
                output.error("valueList: expected=${expected.valueList}, actual=${actual.valueList}")
            }
            if (expected.valueMap != actual.valueMap) {
                output.error("valueMap: expected=${expected.valueMap}, actual=${actual.valueMap}")
            }
            if (expected.nested != actual.nested) {
                output.error("nested: expected=${expected.nested}, actual=${actual.nested}")
            }
            if (expected.nestedList != actual.nestedList) {
                output.error("nestedList: expected=${expected.nestedList}, actual=${actual.nestedList}")
            }
            if (expected.nestedMap != actual.nestedMap) {
                output.error("nestedMap: expected=${expected.nestedMap}, actual=${actual.nestedMap}")
            }
            if (expected.nullable != actual.nullable) {
                output.error("nullable: expected=${expected.nullable}, actual=${actual.nullable}")
            }
            if (expected.nullableList !=
                actual.nullableList
            ) {
                output.error("nullableList: expected=${expected.nullableList}, actual=${actual.nullableList}")
            }
            if (
                expected.nullableArray.contentEquals(actual.nullableArray).not()
            ) {
                output.error("nullableArray: expected=${expected.nullableArray}, actual=${actual.nullableArray}")
            }
            if (expected.nullableArrayDeque !=
                actual.nullableArrayDeque
            ) {
                output.error("nullableArrayDeque: expected=${expected.nullableArrayDeque}, actual=${actual.nullableArrayDeque}")
            }
            if (expected.nullableSet !=
                actual.nullableSet
            ) {
                output.error("nullableSet: expected=${expected.nullableSet}, actual=${actual.nullableSet}")
            }
            if (expected.nullableMap !=
                actual.nullableMap
            ) {
                output.error("nullableMap: expected=${expected.nullableMap}, actual=${actual.nullableMap}")
            }
            if (expected.nullableMap2 != actual.nullableMap2) {
                output.error(
                    "nullableMap2: expected=${expected.nullableMap2}, actual=${actual.nullableMap2}",
                )
            }
            if (expected.nullableListMap != actual.nullableListMap) {
                output.error("nullableListMap: expected=${expected.nullableListMap}, actual=${actual.nullableListMap}")
            }
            if (expected.nullableMapList != actual.nullableMapList) {
                output.error("nullableMapList: expected=${expected.nullableMapList}, actual=${actual.nullableMapList}")
            }
            if (expected.nullableListNullableMap != actual.nullableListNullableMap) {
                output.error(
                    "nullableListNullableMap: expected=${expected.nullableListNullableMap}, actual=${actual.nullableListNullableMap}",
                )
            }
            if (expected.nullableListNullableMap != actual.nullableListNullableMap) {
                output.error(
                    "nullableListNullableMap: expected=${expected.nullableListNullableMap}, actual=${actual.nullableListNullableMap}",
                )
            }
            if (expected.pathName != actual.pathName) {
                output.error("pathName: expected=${expected.pathName}, actual=${actual.pathName}")
            }
            if (expected.listPathName != actual.listPathName) {
                output.error("listPathName: expected=${expected.listPathName}, actual=${actual.listPathName}")
            }
            if (expected.mapPathName != actual.mapPathName) {
                output.error("mapPathName: expected=${expected.mapPathName}, actual=${actual.mapPathName}")
            }
        }
    }

    private fun testDefaultSerializer() {
        output.info("Test default serializer:")

        val config = HasDefaultConfigLoader.loadFromString("")
        if (config.value != "default") {
            output.error("[default] value: expected=default, actual=${config.value}")
        } else {
            output.info("value: OK")
        }

        val config2 = HasDefaultConfigLoader.loadFromString("value: test")
        if (config2.value != "test") {
            output.error("[default] value: expected=test, actual=${config2.value}")
        } else {
            output.info("value: OK")
        }
    }

    private fun testSealedSerializer() {
        output.info("Test sealed serializer:")

        listOf(
            Pair(
                """
                $: dev.s7a.example.config.SealedTestConfig.A
                a: text1
                value: 5
                enum: TestA
                """.trimIndent(),
                SealedTestConfig.A("text1", 5, SealedTestConfig.A.Enum.TestA),
            ),
            Pair(
                """
                $: dev.s7a.example.config.SealedTestConfig.A
                a: text1
                value: 5
                enum: TestA
                """.trimIndent(),
                SealedTestConfig.A("text1", 5, SealedTestConfig.A.Enum.TestA),
            ),
            Pair(
                """
                $: dev.s7a.example.config.SealedTestConfig.B.B1
                type: dev.s7a.example.config.SealedTestConfig.B.B1
                b1: text2
                value: world
                """.trimIndent(),
                SealedTestConfig.B.B1("text2", SealedTestConfig.B.B1.Enum.TestB1),
            ),
            Pair(
                """
                $: dev.s7a.example.config.SealedTestConfig.B.B2
                type: dev.s7a.example.config.SealedTestConfig.B.B2
                value: text3
                """.trimIndent(),
                SealedTestConfig.B.B2("text3"),
            ),
            Pair(
                """
                $: dev.s7a.example.config.SealedTestConfig.C.C1
                c1: text4
                """.trimIndent(),
                SealedTestConfig.C.C1("text4"),
            ),
        ).forEach { (yaml, expected) ->
            val config = SealedTestConfigLoader.loadFromString(yaml)
            if (config != expected) {
                output.error("SealedTestConfig: expected=$expected, actual=$config")
            } else {
                output.info("SealedTestConfig: OK")
            }

            val actual = SealedTestConfigLoader.saveToString(config)
            if (actual != yaml) {
                output.error("SealedTestConfig: expected=$yaml, actual=$actual")
            } else {
                output.info("SealedTestConfig: OK")
            }
        }

        listOf(
            Pair(
                """
                type: b1
                b1: text2
                value: world
                """.trimIndent(),
                SealedTestConfig.B.B1("text2", SealedTestConfig.B.B1.Enum.TestB1),
            ),
            Pair(
                """
                type: dev.s7a.example.config.SealedTestConfig.B.B2
                b2: text3
                """.trimIndent(),
                SealedTestConfig.B.B2("text3"),
            ),
        ).forEach { (yaml, expected) ->
            val config = SealedTestConfigBLoader.loadFromString(yaml)
            if (config != expected) {
                output.error("SealedTestConfigB: expected=$expected, actual=$config")
            } else {
                output.info("SealedTestConfigB: OK")
            }

            val actual = SealedTestConfigBLoader.saveToString(config)
            if (actual != yaml) {
                output.error("SealedTestConfigB: expected=$yaml, actual=$actual")
            } else {
                output.info("SealedTestConfigB: OK")
            }
        }
    }
}
