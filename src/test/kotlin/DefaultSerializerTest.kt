import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.model.IntVector2
import dev.s7a.ktconfig.model.IntVector3
import dev.s7a.ktconfig.model.LazyBlock
import dev.s7a.ktconfig.model.LazyChunk
import dev.s7a.ktconfig.model.LazyLocation
import dev.s7a.ktconfig.model.Vector2
import dev.s7a.ktconfig.model.Vector3
import dev.s7a.ktconfig.saveKtConfigString
import org.bukkit.Location
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
    fun lazyBlock() {
        data class Data(val data: LazyBlock?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: test, -1, 3, 5\n", saveKtConfigString(Data(LazyBlock("test", -1, 3, 5))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, -1"))
        assertEquals(Data(null), ktConfigString("data: null, -1, 3"))
        assertEquals(Data(LazyBlock("null", -1, 3, 5)), ktConfigString("data: null, -1, 3, 5"))
        assertEquals(null, LazyBlock("null", -1, 3, 5).get())
        assertEquals(Data(null), ktConfigString("data: test"))
        assertEquals(Data(null), ktConfigString("data: test, -1"))
        assertEquals(Data(null), ktConfigString("data: test, -1, 3"))
        assertEquals(Data(LazyBlock("test", -1, 3, 5)), ktConfigString("data: test, -1, 3, 5"))
        assertEquals(world.getBlockAt(-1, 3, 5), LazyBlock("test", -1, 3, 5).get())
    }

    @Test
    fun lazyChunk() {
        data class Data(val data: LazyChunk?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: test, -1, 5\n", saveKtConfigString(Data(LazyChunk("test", -1, 5))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, -1"))
        assertEquals(Data(LazyChunk("null", -1, 5)), ktConfigString("data: null, -1, 5"))
        assertEquals(null, LazyChunk("null", -1, 5).get())
        assertEquals(Data(null), ktConfigString("data: test"))
        assertEquals(Data(null), ktConfigString("data: test, -1"))
        assertEquals(Data(LazyChunk("test", -1, 5)), ktConfigString("data: test, -1, 5"))
        assertEquals(world.getChunkAt(-1, 5), LazyChunk("test", -1, 5).get())
    }

    @Test
    fun lazyLocation() {
        data class Data(val data: LazyLocation?)

        val world = server.addSimpleWorld("test")
        assertEquals("data: null, 5.0, 2.0, -1.5\n", saveKtConfigString(Data(LazyLocation("null", 5.0, 2.0, -1.5))))
        assertEquals("data: test, 5.0, 2.0, -1.5\n", saveKtConfigString(Data(LazyLocation("test", 5.0, 2.0, -1.5))))
        assertEquals("data: test, 5.0, 2.0, -1.5, 90.0, 31.5\n", saveKtConfigString(Data(LazyLocation("test", 5.0, 2.0, -1.5, 90F, 31.5F))))
        assertEquals(Data(null), ktConfigString("data: null"))
        assertEquals(Data(null), ktConfigString("data: null, 5"))
        assertEquals(Data(null), ktConfigString("data: null, 5, 2"))
        assertEquals(Data(LazyLocation("null", 5.0, 2.0, -1.5)), ktConfigString("data: null, 5, 2, -1.5"))
        assertEquals(Data(LazyLocation("null", 5.0, 2.0, -1.5, 30F, 0F)), ktConfigString("data: null, 5, 2, -1.5, 30"))
        assertEquals(Location(null, 5.0, 2.0, -1.5, 30F, 10F), LazyLocation("null", 5.0, 2.0, -1.5, 30F, 10F).get())
        assertEquals(Data(LazyLocation("test", 5.0, 2.0, -1.5)), ktConfigString("data: test, 5, 2, -1.5"))
        assertEquals(Data(LazyLocation("test", 5.0, 2.0, -1.5)), ktConfigString("data: test, 5, 2, -1.5, 0, 0"))
        assertEquals(Data(LazyLocation("test", 5.0, 2.0, -1.5, 90F, 31.5F)), ktConfigString("data: test, 5, 2, -1.5, 90, 31.5"))
        assertEquals(Location(world, 5.0, 2.0, -1.5, 30F, 10F), LazyLocation("test", 5.0, 2.0, -1.5, 30F, 10F).get())
    }

    @Test
    fun intVector2() {
        data class Data(val data: IntVector2?)

        assertEquals("data: 5, -1\n", saveKtConfigString(Data(IntVector2(5, -1))))
        assertEquals(Data(null), ktConfigString("data: 5"))
        assertEquals(Data(IntVector2(5, -1)), ktConfigString("data: 5, -1"))
    }

    @Test
    fun intVector3() {
        data class Data(val data: IntVector3?)

        assertEquals("data: 5, 2, -1\n", saveKtConfigString(Data(IntVector3(5, 2, -1))))
        assertEquals(Data(null), ktConfigString("data: 5"))
        assertEquals(Data(IntVector3(5, 2, -1)), ktConfigString("data: 5, 2, -1"))
    }

    @Test
    fun vector2() {
        data class Data(val data: Vector2?)

        assertEquals("data: 5.0, -1.5\n", saveKtConfigString(Data(Vector2(5.0, -1.5))))
        assertEquals(Data(null), ktConfigString("data: 5"))
        assertEquals(Data(Vector2(5.0, -1.5)), ktConfigString("data: 5, -1.5"))
    }

    @Test
    fun vector3() {
        data class Data(val data: Vector3?)

        assertEquals("data: 5.0, 2.0, -1.5\n", saveKtConfigString(Data(Vector3(5.0, 2.0, -1.5))))
        assertEquals(Data(null), ktConfigString("data: 5"))
        assertEquals(Data(Vector3(5.0, 2.0, -1.5)), ktConfigString("data: 5, 2, -1.5"))
    }
}
