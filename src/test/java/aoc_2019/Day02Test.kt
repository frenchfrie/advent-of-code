package aoc_2019

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

internal class Day02Test {

    @Test
    fun solve() {
        assertEquals(listOf(2,0,0,0,99), Day02().solve(Stream.of(1,0,0,0,99)))

        assertEquals(listOf(2,3,0,6,99), Day02().solve(Stream.of(2,3,0,3,99)))

        assertEquals(listOf(2,4,4,5,99,9801), Day02().solve(Stream.of(2,4,4,5,99,0)))

        assertEquals(listOf(30,1,1,4,2,5,6,0,99), Day02().solve(Stream.of(1,1,1,4,99,5,6,0,99)))
    }

    @Test
    internal fun solve01() {
        var counter = AtomicInteger()

        println(Day02().solve(Aoc2019TestUtils.getInputOnLine(2).map {
            input ->
                if (counter.get() == 1) {
                    counter.incrementAndGet()
                    12
                } else if (counter.get() == 2) {
                    counter.incrementAndGet()
                    2
                } else {
                    counter.incrementAndGet()
                    input
                }
            }
        ))
    }
}