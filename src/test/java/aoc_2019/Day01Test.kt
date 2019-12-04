package aoc_2019

import org.junit.jupiter.api.Test
import java.util.stream.Stream
import kotlin.test.assertEquals

internal class Day01Test {

    @org.junit.jupiter.api.Test
    fun solve_test_case() {
        assertEquals(2, Day01().solve(Stream.of("12")))
        assertEquals(2, Day01().solve(Stream.of("14")))
        assertEquals(654, Day01().solve(Stream.of("1969")))
        assertEquals(33583, Day01().solve(Stream.of("100756")))
        assertEquals(33583 + 654 + 2 + 2, Day01().solve(Stream.of("12", "14", "1969", "100756")))
    }

    @Test
    internal fun solve_myInput() {
        println(Day01().solve(Aoc2019TestUtils.getInput(1)))
    }

    @Test
    internal fun solve2_myInput() {
        println(Day01().solve2(Aoc2019TestUtils.getInput(1)))
    }
}
