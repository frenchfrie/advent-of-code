package aoc_2019

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
        println(Day02().solve(Aoc2019TestUtils.getInputOnLine(2), 2, 12))
    }

    @Test
    internal fun solve02() {
        println(Day02().solve02(Aoc2019TestUtils.getInputOnLine(2)))
    }
}