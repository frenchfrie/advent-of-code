package aoc_2019

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class Day03Test {

    @Test
    fun test1() {
        assertEquals(
            159, Day03().distanceFromClosestIntersection(
                "R75,D30,R83,U83,L12,D49,R71,U7,L72\n" +
                        "U62,R66,U55,R34,D71,R55,D58,R83"
            )
        )
    }

    @Test
    fun test2() {
        assertEquals(
            135, Day03().distanceFromClosestIntersection(
                "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51\n" +
                        "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
            )
        )
    }

    @Test
    fun test() {
        val day03 = Day03()
        val input = Aoc2019TestUtils.getRawInput(3)
        assertEquals(0, day03.distanceFromClosestIntersection(input))
    }
}
