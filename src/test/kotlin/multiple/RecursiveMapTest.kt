package multiple

import dev.s7a.ktconfig.ktConfigString
import dev.s7a.ktconfig.saveKtConfigString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class RecursiveMapTest :
    FunSpec({
        data class Config(
            val index: Int,
            val recursive: Map<String, Config>,
        )

        test("should get all values from recursive map config") {
            ktConfigString<Config>(
                """
                index: 1
                recursive:
                  child1:
                    index: 2
                    recursive:
                      child1.1:
                        index: 3
                        recursive:
                          grandchild1.1.1:
                            index: 4
                          grandchild1.1.2:
                            index: 5
                  child2:
                    index: 6
                    recursive:
                      child2.1:
                        index: 7
                      child2.2:
                        index: 8
                """.trimIndent(),
            ) shouldBe
                Config(
                    index = 1,
                    recursive =
                        mapOf(
                            "child1" to
                                Config(
                                    index = 2,
                                    recursive =
                                        mapOf(
                                            "child1.1" to
                                                Config(
                                                    index = 3,
                                                    recursive =
                                                        mapOf(
                                                            "grandchild1.1.1" to
                                                                Config(
                                                                    index = 4,
                                                                    recursive = emptyMap(),
                                                                ),
                                                            "grandchild1.1.2" to
                                                                Config(
                                                                    index = 5,
                                                                    recursive = emptyMap(),
                                                                ),
                                                        ),
                                                ),
                                        ),
                                ),
                            "child2" to
                                Config(
                                    index = 6,
                                    recursive =
                                        mapOf(
                                            "child2.1" to
                                                Config(
                                                    index = 7,
                                                    recursive = emptyMap(),
                                                ),
                                            "child2.2" to
                                                Config(
                                                    index = 8,
                                                    recursive = emptyMap(),
                                                ),
                                        ),
                                ),
                        ),
                )
        }

        test("should save all values to recursive map config") {
            saveKtConfigString(
                Config(
                    index = 1,
                    recursive =
                        mapOf(
                            "child1" to
                                Config(
                                    index = 2,
                                    recursive =
                                        mapOf(
                                            "child1.1" to
                                                Config(
                                                    index = 3,
                                                    recursive =
                                                        mapOf(
                                                            "grandchild1.1.1" to
                                                                Config(
                                                                    index = 4,
                                                                    recursive = emptyMap(),
                                                                ),
                                                            "grandchild1.1.2" to
                                                                Config(
                                                                    index = 5,
                                                                    recursive = emptyMap(),
                                                                ),
                                                        ),
                                                ),
                                        ),
                                ),
                            "child2" to
                                Config(
                                    index = 6,
                                    recursive =
                                        mapOf(
                                            "child2.1" to
                                                Config(
                                                    index = 7,
                                                    recursive = emptyMap(),
                                                ),
                                            "child2.2" to
                                                Config(
                                                    index = 8,
                                                    recursive = emptyMap(),
                                                ),
                                        ),
                                ),
                        ),
                ),
            ) shouldBe
                """
                index: 1
                recursive:
                  child1:
                    index: 2
                    recursive:
                      child1.1:
                        index: 3
                        recursive:
                          grandchild1.1.1:
                            index: 4
                          grandchild1.1.2:
                            index: 5
                  child2:
                    index: 6
                    recursive:
                      child2.1:
                        index: 7
                      child2.2:
                        index: 8
                
                """.trimIndent()
        }
    })
