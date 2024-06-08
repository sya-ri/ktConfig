package utils

import dev.s7a.ktconfig.saveKtConfigString
import kotlin.test.assertEquals

inline fun <reified T : Any> assertSaveKtConfigString(
    expected: String,
    data: T,
) {
    assertEquals(expected, saveKtConfigString(data))
}
