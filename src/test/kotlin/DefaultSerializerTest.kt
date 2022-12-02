import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import dev.s7a.ktconfig.serializer.BlockString
import dev.s7a.ktconfig.serializer.ChunkString
import dev.s7a.ktconfig.serializer.LocationString
import dev.s7a.ktconfig.serializer.VectorString
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultSerializerTest {
    private lateinit var server: ServerMock

    @BeforeTest
    fun setup() {
        server = MockBukkit.mock()
    }

    @AfterTest
    fun teardown() {
        MockBukkit.unmock()
    }

    @Test
    fun block() {
        data class Data(val data: BlockString?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: test, -1, 3, 5\n", saveKtConfigString(Data(world.getBlockAt(-1, 3, 5))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, -1"))
        assertEquals(Data(null), ktConfigString("data: null, -1, 3"))
        assertEquals(Data(null), ktConfigString("data: null, -1, 3, 5"))
        assertEquals(Data(null), ktConfigString("data: test"))
        assertEquals(Data(null), ktConfigString("data: test, -1"))
        assertEquals(Data(null), ktConfigString("data: test, -1, 3"))
        assertEquals(Data(world.getBlockAt(-1, 3, 5)), ktConfigString("data: test, -1, 3, 5"))
    }

    @Test
    fun chunk() {
        data class Data(val data: ChunkString?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: test, -1, 5\n", saveKtConfigString(Data(world.getChunkAt(-1, 5))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, -1"))
        assertEquals(Data(null), ktConfigString("data: null, -1, 5"))
        assertEquals(Data(null), ktConfigString("data: test"))
        assertEquals(Data(null), ktConfigString("data: test, -1"))
        assertEquals(Data(world.getChunkAt(-1, 5)), ktConfigString("data: test, -1, 5"))
    }

    @Test
    fun location() {
        data class Data(val data: LocationString?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: null, 5.0, 2.0, -1.5\n", saveKtConfigString(Data(Location(null, 5.0, 2.0, -1.5))))
        assertEquals("data: test, 5.0, 2.0, -1.5\n", saveKtConfigString(Data(Location(world, 5.0, 2.0, -1.5))))
        assertEquals("data: test, 5.0, 2.0, -1.5, 90.0, 31.5\n", saveKtConfigString(Data(Location(world, 5.0, 2.0, -1.5, 90F, 31.5F))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, 5"))
        assertEquals(Data(null), ktConfigString("data: null, 5, 2"))
        assertEquals(Data(Location(null, 5.0, 2.0, -1.5)), ktConfigString("data: null, 5, 2, -1.5"))
        assertEquals(Data(Location(null, 5.0, 2.0, -1.5, 30F, 0F)), ktConfigString("data: null, 5, 2, -1.5, 30"))
        assertEquals(Data(Location(world, 5.0, 2.0, -1.5)), ktConfigString("data: test, 5, 2, -1.5"))
        assertEquals(Data(Location(world, 5.0, 2.0, -1.5)), ktConfigString("data: test, 5, 2, -1.5, 0, 0"))
        assertEquals(Data(Location(world, 5.0, 2.0, -1.5, 90F, 31.5F)), ktConfigString("data: test, 5, 2, -1.5, 90, 31.5"))
    }

    @Test
    fun vector() {
        data class Data(val data: VectorString?)

        assertEquals("data: 5.0, 2.0, -1.5\n", saveKtConfigString(Data(Vector(5.0, 2.0, -1.5))))
        assertEquals(Data(null), ktConfigString("data: 5"))
        assertEquals(Data(null), ktConfigString("data: 5, 2"))
        assertEquals(Data(Vector(5.0, 2.0, -1.5)), ktConfigString("data: 5, 2, -1.5"))
    }
}
