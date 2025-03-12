package listofvararg

import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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

    // First element benchmark
    @Benchmark
    fun listFirst(blackhole: Blackhole, state: ListState) {
        val first = state.list.first()
        blackhole.consume(first)
    }

    // Chain of transformations
    @Benchmark
    fun listChain(blackhole: Blackhole, state: ListState) {
        val result = state.list
            .map { it * 3 }
            .filter { (it and 1) == 0 }
            .map { it + 7 }
            .firstOrNull()
        blackhole.consume(result)
    }

    // Real-world scenario
    @Benchmark
    fun listRealWorld(blackhole: Blackhole, state: ListState) {
        val baseValue = 75
        val result = state.list
            .map { it + baseValue }
            .map { it * 3 }
            .filter { (it and 1) == 0 }
            .map { it.toString() }
            .map { it.length }
            .filter { it > 1 }
            .sumOf { it + 1 }
        blackhole.consume(result)
    }

    /*
    // Real-world scenario
    @Benchmark
    fun sequenceRealWorld(blackhole: Blackhole, state: SequenceState) {
        val baseValue = 75
        val result = state.sequence
            .map { it + baseValue }
            .map { it * 3 }
            .filter { (it and 1) == 0 }
            .map { it.toString() }
            .map { it.length }
            .sum()
        blackhole.consume(result)
    }
     */

    @State(Scope.Thread)
    open class ListState {
        @Param("default", "vararg")
        private lateinit var type: String

        lateinit var list: List<Int>

        @Setup
        fun setup() {
            val element = Random.nextInt()
            list = when (type) {
                "default" -> listOf(element)
                "vararg" -> listOfVararg(element)
                else -> throw IllegalArgumentException("Unknown list type: $type")
            }
        }
    }
}