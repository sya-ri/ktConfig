import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyTest {
    @Test
    fun public_field() {
        data class Data(val value1: Int) {
            val value2 = 5
        }

        assertEquals(
            """
                value1: 3
                value2: 5
                
            """.trimIndent(),
            saveKtConfigString(Data(3))
        )
    }

    @Test
    fun private_field() {
        data class Data(private val value1: Int) {
            private val value2 = 5
        }

        assertEquals(
            """
                value1: 3
                value2: 5
                
            """.trimIndent(),
            saveKtConfigString(Data(3))
        )
    }

    @Test
    fun another_field() {
        class Data(value1: Int) {
            val value2 = value1
        }

        assertEquals(
            """
                value2: 3
                
            """.trimIndent(),
            saveKtConfigString(Data(3))
        )
    }

    @Test
    fun getter() {
        data class Data(private val value1: Int) {
            val value2
                get() = 5
        }

        assertEquals(
            """
                value1: 3
                
            """.trimIndent(),
            saveKtConfigString(Data(3))
        )
    }

    private interface Base {
        val value2: Int
    }

    @Test
    fun `interface`() {
        class Data(val value1: Int, override val value2: Int) : Base

        assertEquals(
            """
                    value1: 3
                    value2: 5
                    
            """.trimIndent(),
            saveKtConfigString(Data(3, 5))
        )
        assertEquals("", saveKtConfigString<Base>(Data(3, 5)))
    }

    @Test
    fun `open class`() {
        open class Base(val value3: Int) {
            open val value2: Int = 9
        }
        class Data(val value1: Int, override val value2: Int) : Base(7)

        assertEquals(
            """
                value1: 3
                value2: 5
                value3: 7
                
            """.trimIndent(),
            saveKtConfigString(Data(3, 5))
        )
        assertEquals(
            """
                value2: 9
                value3: 7
                
            """.trimIndent(),
            saveKtConfigString(Base(7))
        )
        assertEquals(
            """
                value2: 5
                value3: 7
                
            """.trimIndent(),
            saveKtConfigString<Base>(Data(3, 5))
        )
    }

    @Test
    fun `abstract class`() {
        abstract class Base(val value3: Int) {
            abstract val value2: Int
        }
        class Data(val value1: Int, override val value2: Int) : Base(7)

        assertEquals(
            """
                value1: 3
                value2: 5
                value3: 7
                
            """.trimIndent(),
            saveKtConfigString(Data(3, 5))
        )
        assertEquals(
            """
                value3: 7
                
            """.trimIndent(),
            saveKtConfigString<Base>(Data(3, 5))
        )
    }
}
