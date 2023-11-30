import dev.s7a.ktconfig.KtConfigSetting
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
            assertEquals("Expected kotlin.Int, but null: data", message)
        }
    }

    @Test
    fun generics() {
        data class Data<T>(val data: T)

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data<Int>>("data: null")
        }.run {
            assertEquals("Expected kotlin.Int, but null: data", message)
        }
    }

    @Test
    fun list() {
        data class Data(val data: List<Int>)

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: null", KtConfigSetting(strictListElement = true))
        }.run {
            assertEquals("Expected kotlin.collections.List<kotlin.Int>, but null: data", message)
        }

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: not-null", KtConfigSetting(strictListElement = true))
        }.run {
            assertEquals("Expected kotlin.Int, but kotlin.String(not-null): data[0]", message)
        }

        ktConfigString<Data>("data: []", KtConfigSetting(strictListElement = true))

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: [null]", KtConfigSetting(strictListElement = true))
        }.run {
            assertEquals("Expected kotlin.Int, but null: data[0]", message)
        }

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: [0, null]", KtConfigSetting(strictListElement = true))
        }.run {
            assertEquals("Expected kotlin.Int, but null: data[1]", message)
        }
    }

    @Test
    fun map() {
        data class Data(val data: Map<Int, Int>)

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>("data: null")
        }.run {
            assertEquals("Expected kotlin.collections.Map<kotlin.Int, kotlin.Int>, but null: data", message)
        }

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>(
                """
                data:
                  1: not-null
                """.trimIndent(),
                KtConfigSetting(strictMapElement = true),
            )
        }.run {
            assertEquals("Expected kotlin.Int, but kotlin.String(not-null): data.1", message)
        }

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>(
                """
                data:
                  not-null: 1
                """.trimIndent(),
                KtConfigSetting(strictMapElement = true),
            )
        }.run {
            assertEquals("Expected kotlin.Int, but kotlin.String(not-null): data.not-null(key)", message)
        }

        assertFailsWith<TypeMismatchException> {
            ktConfigString<Data>(
                """
                data:
                  not-null: not-null
                """.trimIndent(),
                KtConfigSetting(strictMapElement = true),
            )
        }.run {
            assertEquals("Expected kotlin.Int, but kotlin.String(not-null): data.not-null(key)", message)
        }
    }
}
