import dev.s7a.ktconfig.serializer.MapSerializer
import dev.s7a.ktconfig.serializer.Serializer
import dev.s7a.ktconfig.serializer.StringSerializer
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

private fun configuration() =
    YamlConfiguration().apply {
        options().pathSeparator(0x00.toChar())
    }

private fun <T> testSerializerKey(
    expected: T,
    serializer: Serializer.Keyable<T>,
) {
    val configuration = configuration()
    val path = "test"
    val value = "test"
    val mapSerializer = MapSerializer(serializer, StringSerializer)
    val mapExpected = mapOf(expected to value)
    mapSerializer.save(configuration, path, mapExpected)
    val actual = mapSerializer.getOrThrow(configuration, path)
    assertEquals(mapExpected, actual)
}

private fun <T> testSerializerValue(
    expected: T,
    serializer: Serializer<T>,
    expectedValues: Map<String, Any>? = null,
    assert: (T, T) -> Unit = { expected, actual -> assertEquals(expected, actual) },
) {
    val configuration = configuration()
    val path = "test"
    serializer.save(configuration, path, expected)
    if (expectedValues != null) {
        assertEquals(expectedValues, configuration.getValues(false))
    }
    val actual = serializer.getOrThrow(configuration, path)
    assert(expected, actual)
}

fun <T> testSerializer(
    expected: T,
    serializer: Serializer<T>,
    expectedValues: Map<String, Any>? = null,
) {
    testSerializerValue(expected, serializer, expectedValues)
}

fun <T> testSerializer(
    expected: T,
    serializer: Serializer.Keyable<T>,
    expectedValues: Map<String, Any>? = null,
) {
    testSerializerKey(expected, serializer)
    testSerializerValue(expected, serializer, expectedValues)
}

fun testSerializer(
    expected: BooleanArray,
    serializer: Serializer<BooleanArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: CharArray,
    serializer: Serializer<CharArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: FloatArray,
    serializer: Serializer<FloatArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: DoubleArray,
    serializer: Serializer<DoubleArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: ByteArray,
    serializer: Serializer<ByteArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: IntArray,
    serializer: Serializer<IntArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: LongArray,
    serializer: Serializer<LongArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: ShortArray,
    serializer: Serializer<ShortArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UByteArray,
    serializer: Serializer<UByteArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UIntArray,
    serializer: Serializer<UIntArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: ULongArray,
    serializer: Serializer<ULongArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UShortArray,
    serializer: Serializer<UShortArray>,
) = testSerializerValue(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}
