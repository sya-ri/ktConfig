import dev.s7a.ktconfig.exception.TypeMismatchException
import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypeMismatchExceptionTest {
    @Test
    fun `not null`() {
        data class Data(val data: Int)

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: null")
        }.run {
            assertEquals("Expected kotlin.Int, but null (data)", message)
        }
    }

    @Test
    fun generics() {
        data class Data<T>(val data: T)

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data<Int>>("data: null")
        }.run {
            assertEquals("Expected kotlin.Int, but null (data)", message)
        }
    }
}
