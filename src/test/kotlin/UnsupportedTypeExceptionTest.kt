import dev.s7a.ktconfig.exception.UnsupportedTypeException
import dev.s7a.ktconfig.ktConfigString
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UnsupportedTypeExceptionTest {
    @Test
    fun javaClass() {
        assertFailsWith<UnsupportedTypeException> {
            ktConfigString<JavaDataClass>("data: string")
        }
    }
}
