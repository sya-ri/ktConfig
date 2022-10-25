import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import dev.s7a.ktconfig.exception.InvalidLocationFormatException
import dev.s7a.ktconfig.serializer.LocationSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Location
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocationSerializerTest {
    @Serializable
    data class Data(@Serializable(with = LocationSerializer::class) val location: Location)

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
                location: "null, 0.1, 1.2, 2.3"
            """.trimIndent(),
            Data(Location(null, 0.1, 1.2, 2.3))
        )
        assertKtConfigDecode(
            """
                location: "test, 0.1, 1.2, 2.3"
            """.trimIndent(),
            Data(Location(world, 0.1, 1.2, 2.3))
        )
        assertKtConfigDecode(
            """
                location: "null, 0.1, 1.2, 2.3, 3.4, 4.5"
            """.trimIndent(),
            Data(Location(null, 0.1, 1.2, 2.3, 3.4F, 4.5F))
        )
        assertKtConfigDecode(
            """
                location: "test, 0.1, 1.2, 2.3, 3.4, 4.5"
            """.trimIndent(),
            Data(Location(world, 0.1, 1.2, 2.3, 3.4F, 4.5F))
        )
    }

    @Test
    fun encode() {
        val world = server.addSimpleWorld("test")
        assertEquals(
            Data(Location(null, 0.1, 1.2, 2.3)),
            ktConfigTest("location: null, 0.1, 1.2, 2.3")
        )
        assertEquals(
            Data(Location(null, 0.1, 1.2, 2.3)),
            ktConfigTest("location: ignore, 0.1, 1.2, 2.3")
        )
        assertEquals(
            Data(Location(world, 0.1, 1.2, 2.3)),
            ktConfigTest("location: test, 0.1, 1.2, 2.3")
        )
        assertEquals(
            Data(Location(null, 0.1, 1.0, 2.1)),
            ktConfigTest("location:    null      , .1,1, 2.1     ")
        )
        assertEquals(
            Data(Location(world, 0.1, 1.0, 2.1)),
            ktConfigTest("location:    test      , .1,1, 2.1     ")
        )
        assertEquals(
            Data(Location(null, 0.1, 1.2, 2.3, 3.4F, 4.5F)),
            ktConfigTest("location: null, 0.1, 1.2, 2.3, 3.4, 4.5")
        )
        assertEquals(
            Data(Location(world, 0.1, 1.2, 2.3, 3.4F, 4.5F)),
            ktConfigTest("location: test, 0.1, 1.2, 2.3, 3.4, 4.5")
        )
    }

    @Test
    fun failEncode() {
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: ''")
        }
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: test,")
        }
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: test, 1.0, 2.1")
        }
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: test, 1.0, 2.1, 3.2, 4.3")
        }
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: test, 1.0, 2.1, 3.2, 4.3, 5.4, 6.5")
        }
        assertFailsWith<InvalidLocationFormatException> {
            ktConfigTest<Data>("location: test 1.0 2.1 3.2 4.3 5.2")
        }
    }
}
