package multiple

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class RecursiveListTest :
    FunSpec({
        data class Config(
            val index: Int,
            val recursive: List<Config>,
        )

        test("should get all values from recursive list config") {
            ktConfigString<Config>(
                """
                index: 1
                recursive:
                  - index: 2
                    recursive:
                      - index: 3
                        recursive:
                          - index: 4
                            recursive: []
                          - index: 5
                            recursive: []
                  - index: 6
                    recursive:
                      - index: 7
                        recursive: []
                      - index: 8
                        recursive: []
                """.trimIndent(),
            ) shouldBe
                Config(
                    index = 1,
                    recursive =
                        listOf(
                            Config(
                                index = 2,
                                recursive =
                                    listOf(
                                        Config(
                                            index = 3,
                                            recursive =
                                                listOf(
                                                    Config(
                                                        index = 4,
                                                        recursive = emptyList(),
                                                    ),
                                                    Config(
                                                        index = 5,
                                                        recursive = emptyList(),
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                            Config(
                                index = 6,
                                recursive =
                                    listOf(
                                        Config(
                                            index = 7,
                                            recursive = emptyList(),
                                        ),
                                        Config(
                                            index = 8,
                                            recursive = emptyList(),
                                        ),
                                    ),
                            ),
                        ),
                )
        }

        test("should save all values to recursive list config") {
            saveKtConfigString(
                Config(
                    index = 1,
                    recursive =
                        listOf(
                            Config(
                                index = 2,
                                recursive =
                                    listOf(
                                        Config(
                                            index = 3,
                                            recursive =
                                                listOf(
                                                    Config(
                                                        index = 4,
                                                        recursive = emptyList(),
                                                    ),
                                                    Config(
                                                        index = 5,
                                                        recursive = emptyList(),
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                            Config(
                                index = 6,
                                recursive =
                                    listOf(
                                        Config(
                                            index = 7,
                                            recursive = emptyList(),
                                        ),
                                        Config(
                                            index = 8,
                                            recursive = emptyList(),
                                        ),
                                    ),
                            ),
                        ),
                ),
            ) shouldBe
                """
                index: 1
                recursive:
                - index: 2
                  recursive:
                  - index: 3
                    recursive:
                    - index: 4
                      recursive: []
                    - index: 5
                      recursive: []
                - index: 6
                  recursive:
                  - index: 7
                    recursive: []
                  - index: 8
                    recursive: []
                
                """.trimIndent()
        }
    })
