import dev.s7a.ktconfig.exception.UnsupportedTypeException
import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UnsupportedTypeExceptionTest {
    @Test
    fun javaClass() {
        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<JavaDataClass>("data: string")
        }.run {
            assertEquals("JavaDataClass is unsupported type (primary constructor must be defined)", message)
        }
    }

    @Test
    fun javaClassNest() {
        data class Data(val data: JavaDataClass)

        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("data: string")
        }.run {
            assertEquals("JavaDataClass is unsupported type (primary constructor must be defined)", message)
        }
    }

    @Test
    fun mapKey() {
        data class Data(val data: Map<String?, String>)

        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("")
        }.run {
            assertEquals("kotlin.String? is unsupported type (key must be not-null)", message)
        }
        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("data: nulll")
        }.run {
            assertEquals("kotlin.String? is unsupported type (key must be not-null)", message)
        }
        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("data: {}")
        }.run {
            assertEquals("kotlin.String? is unsupported type (key must be not-null)", message)
        }
        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("data: {'a': 'b'}")
        }.run {
            assertEquals("kotlin.String? is unsupported type (key must be not-null)", message)
        }
    }

    @Test
    fun mapKeyNest() {
        data class Data1(val data: Map<String?, String>)

        data class Data(val data: Data1)

        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<Data>("")
        }.run {
            assertEquals("kotlin.String? is unsupported type (key must be not-null)", message)
        }
    }
}
