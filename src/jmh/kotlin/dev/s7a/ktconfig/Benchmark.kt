package dev.s7a.ktconfig

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Suppress("unused")
open class Benchmark {
    @Benchmark
    fun single(): Any? {
        data class Data(
            val value: String,
        )

        return ktConfigString<Data>("value: benchmark")
    }

    @Benchmark
    fun saveSingle(): String {
        data class Data(
            val value: String,
        )

        return saveKtConfigString<Data>(Data("benchmark"))
    }

    @Benchmark
    fun list(): Any? {
        data class Data(
            val value: List<String>,
        )

        return ktConfigString<Data>(
            """
            value:
            - text
            - 1
            - 9223372036854775807
            - テスト
            - ""
            """.trimIndent(),
        )
    }

    @Benchmark
    fun saveList(): String {
        data class Data(
            val value: List<String>,
        )

        return saveKtConfigString<Data>(
            Data(
                listOf(
                    "text",
                    "1",
                    "9223372036854775807",
                    "テスト",
                ),
            ),
        )
    }

    @Benchmark
    fun map(): Any? {
        data class Data(
            val value: Map<String, String>,
        )

        return ktConfigString<Data>(
            """
            value:
              key: value
              some: text
              number: 1
              long-number: 9223372036854775807
            """.trimIndent(),
        )
    }

    @Benchmark
    fun saveMap(): String {
        data class Data(
            val value: Map<String, String>,
        )

        return saveKtConfigString<Data>(
            Data(
                mapOf(
                    "key" to "value",
                    "some" to "text",
                    "number" to "1",
                    "long-number" to "9223372036854775807",
                ),
            ),
        )
    }

    @Benchmark
    fun recursive(): Any? {
        data class Data(
            val value: Data?,
        )

        return ktConfigString<Data>(
            """
            value:
              value:
                value:
                  value:
                    value:
                      value:
                        value:
                          value:
                            value:
                              value: null
            """.trimIndent(),
        )
    }

    @Benchmark
    fun saveRecursive(): String {
        data class Data(
            val value: Data?,
        )

        return saveKtConfigString<Data>(
            Data(
                Data(
                    Data(
                        Data(
                            Data(
                                Data(
                                    Data(
                                        Data(
                                            Data(
                                                Data(
                                                    null,
                                                ),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
