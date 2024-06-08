package utils

import dev.s7a.ktconfig.ktConfigString
import kotlin.test.assertEquals

inline fun <reified T : Any?> assertKtConfigString(
    expected: T,
    yaml: String,
) {
    assertEquals(expected, ktConfigString(yaml))
}
