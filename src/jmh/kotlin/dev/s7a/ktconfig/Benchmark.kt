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
}
