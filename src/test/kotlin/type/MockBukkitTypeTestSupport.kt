package type

import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

inline fun <T> withMockBukkit(block: ServerMock.() -> T): T {
    val server = MockBukkit.mock()
    return try {
        server.block()
    } finally {
        MockBukkit.unmock()
    }
}
