package multiple

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class RecursiveTest :
    FunSpec({
        data class Config(
            val index: Int,
            val recursive: Config?,
        )

        test("should get all values from recursive config") {
            ktConfigString<Config>(
                """
                index: 1
                recursive:
                  index: 2
                  recursive:
                    index: 3
                    recursive:
                      index: 4
                      recursive: null
                """.trimIndent(),
            ) shouldBe
                Config(
                    index = 1,
                    recursive =
                        Config(
                            index = 2,
                            recursive =
                                Config(
                                    index = 3,
                                    recursive =
                                        Config(
                                            index = 4,
                                            recursive = null,
                                        ),
                                ),
                        ),
                )
        }

        test("should save all values to recursive config") {
            saveKtConfigString(
                Config(
                    index = 1,
                    recursive =
                        Config(
                            index = 2,
                            recursive =
                                Config(
                                    index = 3,
                                    recursive =
                                        Config(
                                            index = 4,
                                            recursive = null,
                                        ),
                                ),
                        ),
                ),
            ) shouldBe
                """
                index: 1
                recursive:
                  index: 2
                  recursive:
                    index: 3
                    recursive:
                      index: 4
                
                """.trimIndent()
        }
    })
