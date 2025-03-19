package listofvararg

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 1)
open class ListOperationsBenchmark {

    // Basic creation benchmarks
    @Benchmark
    fun defaultListOfSingleCreation(blackhole: Blackhole, state: CreationState) {
        repeat(state.count) {
            val list = listOf(1)
            blackhole.consume(list)
        }
    }

    @Benchmark
    fun listOfVarargCreation(blackhole: Blackhole, state: CreationState) {
        repeat(state.count) {
            val list = listOfVararg(1)
            blackhole.consume(list)
        }
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
            .filter { (it and 1) == 1 }
            .map { it + 7 }
            .firstOrNull()
        blackhole.consume(result)
    }

    // Real-world scenario
    @Benchmark
    fun listRealWorld(blackhole: Blackhole, state: ListState) {
        val baseValue = 78
        val result = state.list
            .map { it + baseValue }
            .map { it * 3 }
            .filter { (it and 1) == 1 }
            .map { it.toString() }
            .filter { it.isNotEmpty() }
            .map { it.length }
            .sumOf { it * 2 }
        blackhole.consume(result)
    }

    // Polymorphic call site benchmark
    @Benchmark
    fun polymorphicCallSite(blackhole: Blackhole, state: PolymorphicState) {
        var sum = 0
        for (list in state.lists) {
            sum += list
                .map { it * 2 }
                .filter { it > 0 }
                .sumOf { it + 3 }
        }
        blackhole.consume(sum)
    }

    @State(Scope.Thread)
    open class CreationState {
        @Param("1", "10", "1000", "1000000")
        var count: Int = 0
    }

    @State(Scope.Thread)
    open class ListState {
        @Param("default", "vararg")
        private lateinit var type: String

        lateinit var list: List<Int>

        @Setup
        fun setup() {
            val element = RANDOM.nextInt(0, 1_000_000) * 2 + 1
            list = when (type) {
                "default" -> listOf(element)
                "vararg" -> listOfVararg(element)
                else -> throw IllegalArgumentException("Unknown list type: $type")
            }
        }
    }

    @State(Scope.Thread)
    open class PolymorphicState {
        @Param("default_only", "vararg_only", "mixed")
        private lateinit var scenario: String

        private val elements = List(100) { RANDOM.nextInt(0, 1_000_000) * 2 + 1 }
        lateinit var lists: List<List<Int>>

        @Setup
        fun setup() {
            lists = when (scenario) {
                "default_only" -> elements.map { listOf(it) }
                "vararg_only" -> elements.map { listOfVararg(it) }
                "mixed" -> elements.mapIndexed { index, value ->
                    if (index % 2 == 0) listOf(value) else listOfVararg(value)
                }

                else -> throw IllegalArgumentException("Unknown scenario: $scenario")
            }
        }
    }

    companion object {
        val RANDOM = Random(0xcafebabe)
    }
}