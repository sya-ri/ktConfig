package dev.s7a.example

import dev.s7a.example.config.SerializerTestConfig
import dev.s7a.example.config.SerializerTestConfigLoader
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import kotlin.random.Random

class ExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        testSerializer()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun testSerializer() {
        val expected =
            SerializerTestConfig(
                byte = Random.nextBytes(1)[0],
                char = Random.nextInt(32, 127).toChar(),
                double = Random.nextDouble(),
                float = Random.nextFloat(),
                int = Random.nextInt(),
                long = Random.nextLong(),
                short = Random.nextInt(-32768, 32767).toShort(),
                string = UUID.randomUUID().toString(),
                uByte = Random.nextInt(0, 255).toUByte(),
                uInt = Random.nextInt().toUInt(),
                uLong = Random.nextLong().toULong(),
                uShort = Random.nextInt(0, 65535).toUShort(),
                list = List(5) { UUID.randomUUID().toString() },
                set = setOf(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                arrayDeque = ArrayDeque(List(3) { UUID.randomUUID().toString() }),
                uByteArray = UByteArray(3) { Random.nextInt(0, 255).toUByte() },
                uIntArray = UIntArray(3) { Random.nextInt().toUInt() },
                uLongArray = ULongArray(3) { Random.nextLong().toULong() },
                uShortArray = UShortArray(3) { Random.nextInt(0, 65535).toUShort() },
                nullable = null,
            )

        logger.info("Save SerializerTestConfig:")
        logger.info(expected.toString())

        val yaml = SerializerTestConfigLoader.saveToString(expected)

        logger.info("SerializerTestConfig:\n$yaml")

        val actual = SerializerTestConfigLoader.loadFromString(yaml)

        logger.info("Loaded SerializerTestConfig:")
        logger.info(actual.toString())

        if (expected != actual) {
            logger.info("Differences found:")
            if (expected.byte != actual.byte) logger.info("byte: expected=${expected.byte}, actual=${actual.byte}")
            if (expected.char != actual.char) logger.info("char: expected=${expected.char}, actual=${actual.char}")
            if (expected.double != actual.double) logger.info("double: expected=${expected.double}, actual=${actual.double}")
            if (expected.float != actual.float) logger.info("float: expected=${expected.float}, actual=${actual.float}")
            if (expected.int != actual.int) logger.info("int: expected=${expected.int}, actual=${actual.int}")
            if (expected.long != actual.long) logger.info("long: expected=${expected.long}, actual=${actual.long}")
            if (expected.short != actual.short) logger.info("short: expected=${expected.short}, actual=${actual.short}")
            if (expected.string != actual.string) logger.info("string: expected=${expected.string}, actual=${actual.string}")
            if (expected.uByte != actual.uByte) logger.info("uByte: expected=${expected.uByte}, actual=${actual.uByte}")
            if (expected.uInt != actual.uInt) logger.info("uInt: expected=${expected.uInt}, actual=${actual.uInt}")
            if (expected.uLong != actual.uLong) logger.info("uLong: expected=${expected.uLong}, actual=${actual.uLong}")
            if (expected.uShort != actual.uShort) logger.info("uShort: expected=${expected.uShort}, actual=${actual.uShort}")
            if (expected.list != actual.list) logger.info("list: expected=${expected.list}, actual=${actual.list}")
            if (expected.set != actual.set) logger.info("set: expected=${expected.set}, actual=${actual.set}")
            if (expected.arrayDeque != actual.arrayDeque) {
                logger.info("arrayDeque: expected=${expected.arrayDeque}, actual=${actual.arrayDeque}")
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
            if (expected.nullable != actual.nullable) logger.info("nullable: expected=${expected.nullable}, actual=${actual.nullable}")

            throw AssertionError("SerializerTestConfig is not loaded correctly. expected: $expected, actual: $actual")
        }
    }
}
