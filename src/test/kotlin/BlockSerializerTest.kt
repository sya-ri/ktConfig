import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.s7a.ktconfig.exception.InvalidBlockFormatException
import dev.s7a.ktconfig.exception.WorldNotFoundException
import dev.s7a.ktconfig.serializer.BlockSerializer
import kotlinx.serialization.Serializable
import org.bukkit.block.Block
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BlockSerializerTest {
    @Serializable
    data class Data(@Serializable(with = BlockSerializer::class) val block: Block)

    private lateinit var server: ServerMock

    @BeforeTest
    fun setUp() {
        server = MockBukkit.mock()
    }

    @AfterTest
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun decode() {
        val world = server.addSimpleWorld("test")
        assertKtConfigDecode(
            """
                block: "test, 0, 21, 302"
            """.trimIndent(),
            Data(world.getBlockAt(0, 21, 302))
        )
    }

    @Test
    fun encode() {
        val world = server.addSimpleWorld("test")
        assertEquals(
            Data(world.getBlockAt(0, 21, 302)),
            ktConfigTest("block: test, 0, 21, 302")
        )
        assertEquals(
            Data(world.getBlockAt(0, 21, 302)),
            ktConfigTest("block:    test   ,   0  ,21, 302   ")
        )
    }

    @Test
    fun failEncode() {
        assertFailsWith<InvalidBlockFormatException> {
            ktConfigTest<Data>("block: ''")
        }
        assertFailsWith<InvalidBlockFormatException> {
            ktConfigTest<Data>("block: test,")
        }
        assertFailsWith<InvalidBlockFormatException> {
            ktConfigTest<Data>("block: test, 0, 21")
        }
        assertFailsWith<InvalidBlockFormatException> {
            ktConfigTest<Data>("block: test, 0, 21, 302, 4003")
        }
        assertFailsWith<WorldNotFoundException> {
            ktConfigTest<Data>("block: null, 0, 21, 302")
        }
        assertFailsWith<WorldNotFoundException> {
            ktConfigTest<Data>("block: ignore, 0, 21, 302")
        }
    }
}
