package listofvararg

import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 1)
open class Benchmark {

    // Basic creation benchmarks
    @Benchmark
    fun defaultListOfSingleCreation(blackhole: Blackhole) {
        val list = listOf(1)
        blackhole.consume(list)
    }

    @Benchmark
    fun listOfVarargCreation(blackhole: Blackhole) {
        val list = listOfVararg(1)
        blackhole.consume(list)
    }
}