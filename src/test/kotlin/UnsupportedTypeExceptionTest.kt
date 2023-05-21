import dev.s7a.ktconfig.exception.NullableMapKeyException
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

    @Test
    fun mapKey() {
        data class Data(val data: Map<String?, String>)

        assertFailsWith<NullableMapKeyException> {
            ktConfigString<Data>("data: {'a': 'b'}")
        }
    }
}
