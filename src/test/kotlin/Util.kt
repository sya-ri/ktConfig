
import dev.s7a.ktconfig.ktConfigString
import kotlin.test.assertEquals

interface TestData<T> {
    val data: T
}

inline fun <T, reified S : TestData<T>> assertParse(expected: T, actual: String) {
    assertEquals(expected, ktConfigString<S>("data: $actual")?.data)
}

inline fun <T : Map<*, *>, reified S : TestData<T>> assertMapParse(expected: T, actual: String) {
    assertParse<T, S>(expected, "\n${actual.lines().joinToString("\n") { "  $it" }}")
}
