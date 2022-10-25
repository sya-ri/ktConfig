import dev.s7a.ktconfig.ktConfig
import dev.s7a.ktconfig.saveKtConfig
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.test.assertEquals

fun testConfigFile(content: String): File {
    return createTempFile().toFile().apply {
        writeText(content)
    }
}

inline fun <reified T : Any> ktConfigTest(content: String): T? {
    return ktConfig(testConfigFile(content))
}

inline fun <reified T : Any> assertKtConfigDecode(expected: String, actual: T) {
    val file = createTempFile().toFile()
    saveKtConfig(file, actual)
    assertEquals(expected, file.readText())
}
