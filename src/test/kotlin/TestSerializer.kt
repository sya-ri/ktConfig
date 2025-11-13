import dev.s7a.ktconfig.serializer.Serializer
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

fun <T> testSerializer(
    expected: T,
    serializer: Serializer<T>,
    assert: (T, T) -> Unit = { expected, actual -> assertEquals(expected, actual) },
) {
    val configuration = YamlConfiguration()
    val path = "test"
    configuration.set(path, serializer.serialize(expected))
    val actual = serializer.get(configuration, path)
    assertNotNull(actual)
    assert(expected, actual)
}

fun testSerializer(
    expected: BooleanArray,
    serializer: Serializer<BooleanArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: CharArray,
    serializer: Serializer<CharArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: FloatArray,
    serializer: Serializer<FloatArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: DoubleArray,
    serializer: Serializer<DoubleArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: ByteArray,
    serializer: Serializer<ByteArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: IntArray,
    serializer: Serializer<IntArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: LongArray,
    serializer: Serializer<LongArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

fun testSerializer(
    expected: ShortArray,
    serializer: Serializer<ShortArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UByteArray,
    serializer: Serializer<UByteArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UIntArray,
    serializer: Serializer<UIntArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: ULongArray,
    serializer: Serializer<ULongArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun testSerializer(
    expected: UShortArray,
    serializer: Serializer<UShortArray>,
) = testSerializer(expected, serializer) { expected, actual ->
    assertContentEquals(expected, actual)
}
