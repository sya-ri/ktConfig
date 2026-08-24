package roundtrip

import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

data class RoundTripConfig<T>(
    val value: T,
    val nullable: T?,
    val list: List<T>,
    val nullableList: List<T?>,
    val set: Set<T>,
    val arrayDeque: ArrayDeque<T>,
    val map: Map<String, T>,
    val nullableMap: Map<String, T?>,
)

fun <T> assertRoundTrip(
    expected: T,
    save: (T) -> String,
    load: (String) -> T,
) {
    assertEquals(expected, load(save(expected)))
}

data class ArrayRoundTripConfig<T>(
    val value: Array<T>,
    val nullable: Array<T>?,
    val nullableElements: Array<T?>,
)

fun <T> assertArrayRoundTrip(
    expected: ArrayRoundTripConfig<T>,
    save: (ArrayRoundTripConfig<T>) -> String,
    load: (String) -> ArrayRoundTripConfig<T>,
) {
    val actual = load(save(expected))
    assertContentEquals(expected.value, actual.value)
    assertContentEquals(expected.nullable, actual.nullable)
    assertContentEquals(expected.nullableElements, actual.nullableElements)
}

fun assertBooleanArrayRoundTrip(
    expected: BooleanArray,
    save: (BooleanArrayRoundTrip) -> String,
    load: (String) -> BooleanArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(BooleanArrayRoundTrip(expected))).value)
}

fun assertByteArrayRoundTrip(
    expected: ByteArray,
    save: (ByteArrayRoundTrip) -> String,
    load: (String) -> ByteArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(ByteArrayRoundTrip(expected))).value)
}

fun assertCharArrayRoundTrip(
    expected: CharArray,
    save: (CharArrayRoundTrip) -> String,
    load: (String) -> CharArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(CharArrayRoundTrip(expected))).value)
}

fun assertShortArrayRoundTrip(
    expected: ShortArray,
    save: (ShortArrayRoundTrip) -> String,
    load: (String) -> ShortArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(ShortArrayRoundTrip(expected))).value)
}

fun assertIntArrayRoundTrip(
    expected: IntArray,
    save: (IntArrayRoundTrip) -> String,
    load: (String) -> IntArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(IntArrayRoundTrip(expected))).value)
}

fun assertLongArrayRoundTrip(
    expected: LongArray,
    save: (LongArrayRoundTrip) -> String,
    load: (String) -> LongArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(LongArrayRoundTrip(expected))).value)
}

fun assertFloatArrayRoundTrip(
    expected: FloatArray,
    save: (FloatArrayRoundTrip) -> String,
    load: (String) -> FloatArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(FloatArrayRoundTrip(expected))).value)
}

fun assertDoubleArrayRoundTrip(
    expected: DoubleArray,
    save: (DoubleArrayRoundTrip) -> String,
    load: (String) -> DoubleArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(DoubleArrayRoundTrip(expected))).value)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun assertUByteArrayRoundTrip(
    expected: UByteArray,
    save: (UByteArrayRoundTrip) -> String,
    load: (String) -> UByteArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(UByteArrayRoundTrip(expected))).value)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun assertUShortArrayRoundTrip(
    expected: UShortArray,
    save: (UShortArrayRoundTrip) -> String,
    load: (String) -> UShortArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(UShortArrayRoundTrip(expected))).value)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun assertUIntArrayRoundTrip(
    expected: UIntArray,
    save: (UIntArrayRoundTrip) -> String,
    load: (String) -> UIntArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(UIntArrayRoundTrip(expected))).value)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun assertULongArrayRoundTrip(
    expected: ULongArray,
    save: (ULongArrayRoundTrip) -> String,
    load: (String) -> ULongArrayRoundTrip,
) {
    assertContentEquals(expected, load(save(ULongArrayRoundTrip(expected))).value)
}

inline fun <T> withMockBukkit(block: ServerMock.() -> T): T {
    val server = MockBukkit.mock()
    return try {
        server.block()
    } finally {
        MockBukkit.unmock()
    }
}
