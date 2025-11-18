package dev.s7a.example

import dev.s7a.example.config.SerializerTestConfig
import dev.s7a.example.config.SerializerTestConfigLoader
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.random.Random

@Suppress("unused")
class ExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        testSerializer()
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
            )

        val expected =
            data.copy(
                // Null values in maps are ignored during serialization
                nullableMap = data.nullableMap.filterValues { it != null },
                nullableMap2 = data.nullableMap2.mapValues { map -> map.value.filterValues { it != null } },
            )

        logger.info("Save SerializerTestConfig:")
        logger.info(data.toString())

        val yaml = SerializerTestConfigLoader.saveToString(data)

        logger.info("SerializerTestConfig:\n$yaml")

        val actual = SerializerTestConfigLoader.loadFromString(yaml)

        logger.info("Loaded SerializerTestConfig:")
        logger.info(actual.toString())

        if (expected != actual) {
            logger.info("Differences found:")
            if (expected.string != actual.string) {
                logger.info("string: expected=${expected.string}, actual=${actual.string}")
            }
            if (expected.byte != actual.byte) {
                logger.info("byte: expected=${expected.byte}, actual=${actual.byte}")
            }
            if (expected.char != actual.char) {
                logger.info("char: expected=${expected.char}, actual=${actual.char}")
            }
            if (expected.int != actual.int) {
                logger.info("int: expected=${expected.int}, actual=${actual.int}")
            }
            if (expected.long != actual.long) {
                logger.info("long: expected=${expected.long}, actual=${actual.long}")
            }
            if (expected.short != actual.short) {
                logger.info("short: expected=${expected.short}, actual=${actual.short}")
            }
            if (expected.double != actual.double) {
                logger.info("double: expected=${expected.double}, actual=${actual.double}")
            }
            if (expected.float != actual.float) {
                logger.info("float: expected=${expected.float}, actual=${actual.float}")
            }
            if (expected.uByte != actual.uByte) {
                logger.info("uByte: expected=${expected.uByte}, actual=${actual.uByte}")
            }
            if (expected.uInt != actual.uInt) {
                logger.info("uInt: expected=${expected.uInt}, actual=${actual.uInt}")
            }
            if (expected.uLong != actual.uLong) {
                logger.info("uLong: expected=${expected.uLong}, actual=${actual.uLong}")
            }
            if (expected.uShort != actual.uShort) {
                logger.info("uShort: expected=${expected.uShort}, actual=${actual.uShort}")
            }
            if (expected.boolean != actual.boolean) {
                logger.info("boolean: expected=${expected.boolean}, actual=${actual.boolean}")
            }
            if (expected.uuid != actual.uuid) {
                logger.info("uuid: expected=${expected.uuid}, actual=${actual.uuid}")
            }
            if (expected.itemStack != actual.itemStack) {
                logger.info("itemStack: expected=${expected.itemStack}, actual=${actual.itemStack}")
            }
            if (expected.location != actual.location) {
                logger.info("location: expected=${expected.location}, actual=${actual.location}")
            }
            if (expected.array.contentEquals(actual.array).not()) {
                logger.info("array: expected=${expected.array.contentToString()}, actual=${actual.array.contentToString()}")
            }
            if (expected.byteArray.contentEquals(actual.byteArray).not()) {
                logger.info("byteArray: expected=${expected.byteArray.contentToString()}, actual=${actual.byteArray.contentToString()}")
            }
            if (expected.charArray.contentEquals(actual.charArray).not()) {
                logger.info("charArray: expected=${expected.charArray.contentToString()}, actual=${actual.charArray.contentToString()}")
            }
            if (expected.intArray.contentEquals(actual.intArray).not()) {
                logger.info("intArray: expected=${expected.intArray.contentToString()}, actual=${actual.intArray.contentToString()}")
            }
            if (expected.longArray.contentEquals(actual.longArray).not()) {
                logger.info("longArray: expected=${expected.longArray.contentToString()}, actual=${actual.longArray.contentToString()}")
            }
            if (expected.shortArray.contentEquals(actual.shortArray).not()) {
                logger.info("shortArray: expected=${expected.shortArray.contentToString()}, actual=${actual.shortArray.contentToString()}")
            }
            if (expected.doubleArray.contentEquals(actual.doubleArray).not()) {
                logger.info(
                    "doubleArray: expected=${expected.doubleArray.contentToString()}, actual=${actual.doubleArray.contentToString()}",
                )
            }
            if (expected.floatArray.contentEquals(actual.floatArray).not()) {
                logger.info("floatArray: expected=${expected.floatArray.contentToString()}, actual=${actual.floatArray.contentToString()}")
            }
            if (expected.uByteArray.contentEquals(actual.uByteArray).not()) {
                logger.info("uByteArray: expected=${expected.uByteArray.contentToString()}, actual=${actual.uByteArray.contentToString()}")
            }
            if (expected.uIntArray.contentEquals(actual.uIntArray).not()) {
                logger.info("uIntArray: expected=${expected.uIntArray.contentToString()}, actual=${actual.uIntArray.contentToString()}")
            }
            if (expected.uLongArray.contentEquals(actual.uLongArray).not()) {
                logger.info("uLongArray: expected=${expected.uLongArray.contentToString()}, actual=${actual.uLongArray.contentToString()}")
            }
            if (expected.uShortArray.contentEquals(actual.uShortArray).not()) {
                logger.info(
                    "uShortArray: expected=${expected.uShortArray.contentToString()}, actual=${actual.uShortArray.contentToString()}",
                )
            }
            if (expected.booleanArray.contentEquals(actual.booleanArray).not()) {
                logger.info(
                    "booleanArray: expected=${expected.booleanArray.contentToString()}, actual=${actual.booleanArray.contentToString()}",
                )
            }
            if (expected.list != actual.list) {
                logger.info("list: expected=${expected.list}, actual=${actual.list}")
            }
            if (expected.list2 != actual.list2) {
                logger.info("list2: expected=${expected.list2}, actual=${actual.list2}")
            }
            if (expected.set != actual.set) {
                logger.info("set: expected=${expected.set}, actual=${actual.set}")
            }
            if (expected.array.contentEquals(actual.array).not()) {
                logger.info("array: expected=${expected.array}, actual=${actual.array}")
            }
            if (expected.arrayDeque != actual.arrayDeque) {
                logger.info("arrayDeque: expected=${expected.arrayDeque}, actual=${actual.arrayDeque}")
            }
            if (expected.map != actual.map) {
                logger.info("map: expected=${expected.map}, actual=${actual.map}")
            }
            if (expected.map2 != actual.map2) {
                logger.info("map2: expected=${expected.map2}, actual=${actual.map2}")
            }
            if (expected.listMap != actual.listMap) {
                logger.info("listMap: expected=${expected.listMap}, actual=${actual.listMap}")
            }
            if (expected.mapList != actual.mapList) {
                logger.info("mapList: expected=${expected.mapList}, actual=${actual.mapList}")
            }
            if (expected.enum != actual.enum) {
                logger.info("enum: expected=${expected.enum}, actual=${actual.enum}")
            }
            if (expected.enumList != actual.enumList) {
                logger.info("enumList: expected=${expected.enumList}, actual=${actual.enumList}")
            }
            if (expected.enumMap != actual.enumMap) {
                logger.info("enumMap: expected=${expected.enumMap}, actual=${actual.enumMap}")
            }
            if (expected.value != actual.value) {
                logger.info("value: expected=${expected.value}, actual=${actual.value}")
            }
            if (expected.valueList != actual.valueList) {
                logger.info("valueList: expected=${expected.valueList}, actual=${actual.valueList}")
            }
            if (expected.valueMap != actual.valueMap) {
                logger.info("valueMap: expected=${expected.valueMap}, actual=${actual.valueMap}")
            }
            if (expected.nullable != actual.nullable) {
                logger.info("nullable: expected=${expected.nullable}, actual=${actual.nullable}")
            }
            if (expected.nullableList !=
                actual.nullableList
            ) {
                logger.info("nullableList: expected=${expected.nullableList}, actual=${actual.nullableList}")
            }
            if (
                expected.nullableArray.contentEquals(actual.nullableArray).not()
            ) {
                logger.info("nullableArray: expected=${expected.nullableArray}, actual=${actual.nullableArray}")
            }
            if (expected.nullableArrayDeque !=
                actual.nullableArrayDeque
            ) {
                logger.info("nullableArrayDeque: expected=${expected.nullableArrayDeque}, actual=${actual.nullableArrayDeque}")
            }
            if (expected.nullableSet !=
                actual.nullableSet
            ) {
                logger.info("nullableSet: expected=${expected.nullableSet}, actual=${actual.nullableSet}")
            }
            if (expected.nullableMap !=
                actual.nullableMap
            ) {
                logger.info("nullableMap: expected=${expected.nullableMap}, actual=${actual.nullableMap}")
            }
            if (expected.nullableMap2 != actual.nullableMap2) {
                logger.info(
                    "nullableMap2: expected=${expected.nullableMap2}, actual=${actual.nullableMap2}",
                )
            }
            if (expected.nullableListMap != actual.nullableListMap) {
                logger.info("nullableListMap: expected=${expected.nullableListMap}, actual=${actual.nullableListMap}")
            }
            if (expected.nullableMapList != actual.nullableMapList) {
                logger.info("nullableMapList: expected=${expected.nullableMapList}, actual=${actual.nullableMapList}")
            }
            if (expected.nullableListNullableMap != actual.nullableListNullableMap) {
                logger.info(
                    "nullableListNullableMap: expected=${expected.nullableListNullableMap}, actual=${actual.nullableListNullableMap}",
                )
            }
            if (expected.nullableListNullableMap != actual.nullableListNullableMap) {
                logger.info(
                    "nullableListNullableMap: expected=${expected.nullableListNullableMap}, actual=${actual.nullableListNullableMap}",
                )
            }

            throw AssertionError("SerializerTestConfig is not loaded correctly. expected: $expected, actual: $actual")
        }
    }
}
