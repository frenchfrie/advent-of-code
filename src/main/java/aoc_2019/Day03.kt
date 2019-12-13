package aoc_2019

import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

class Day03 {
    private val log = LoggerFactory.getLogger(javaClass)

    fun distanceFromClosestIntersection(coordinates: String): Int {
        val lineRaw = coordinates.split("\n")
            .stream()
            .map { lr -> lr.split(",") }
            .map { l -> l.map{ elem -> Vector(elem)} }
            .map { l ->
                {
                    val line = ArrayList<Segment>(l.size - 1)
                    val previous = Point(0, 0)
                    for (e in l) {
                        val newPosition = e.move(previous)
                        line.add(Segment(previous, newPosition))
                    }
                    line
                }
            }

        return 0
    }

    private class Vector(raw: String) {
        val length: Int = raw.substring(1).toInt()
        val direction: Char = raw[0]

        fun move(from : Point) : Point {
            return when (direction) {
                'R' -> Point(from.x + length, from.y)
                'L' -> Point(from.x - length, from.y)
                'U' -> Point(from.x, from.y + length)
                'D' -> Point(from.x, from.y - length)
                else -> throw RuntimeException("shit")
            }
        }
    }

    private class Line {
        val elements = ArrayList<Point>()
        fun add(point : Point) = elements.add(point)

        fun intersection(line: Line): List<Point> {
            val intersections = ArrayList<Point>()
            var previous = elements[0]
            for (current in elements) {
                val currentSegment = Segment(previous, current)
                var otherPrevious = line.elements[0]
                for (other in line.elements) {
                    val intersection = currentSegment.intersection(Segment(otherPrevious, other))
                    if (intersection != null) {
                        intersections.add(intersection)
                    }
                }
            }
            return intersections
        }
    }

    private data class Segment(val a: Point, val b: Point) {
        fun intersection(segment: Segment): Point? {
            val intersectX : Int
            val intersectY : Int
            if (this.isHoriz()) {
                intersectY = this.a.y
                if (segment.isVert()) {
                    intersectX = segment.a.x
                    if ((min(this.a.x, this.b.x) < intersectX && intersectX < max(this.a.x, this.b.x))
                        && (min(segment.a.y, segment.b.y) < intersectY && intersectY < max(segment.a.y, segment.b.y))) {
                        return Point(intersectX, intersectY)
                    }
                }
            } else {
                intersectX = this.a.y
                if (segment.isHoriz()) {
                    intersectY = segment.a.x
                    if ((min(this.a.x, this.b.x) < intersectX && intersectX < max(this.a.x, this.b.x))
                        && (min(segment.a.y, segment.b.y) < intersectY && intersectY < max(segment.a.y, segment.b.y))) {
                        return Point(intersectX, intersectY)
                    }
                }
            }
            return null
        }

        private fun isHoriz() = a.x == b.x
        private fun isVert() = a.y == b.y


        fun lowerLeftPoint(): Point {
            return if (a.isLeftOf(b)) {
                if (a.isUnderOf(b)) {
                    a
                } else {
                    throw IllegalStateException("should not happen")
                }
            } else {
                if (a.isUnderOf(b)) {
                    throw IllegalStateException("should not happen")
                } else {
                    b
                }
            }
        }

        fun upperRightPoint(): Point {
            return if (this.a == lowerLeftPoint()) this.b else this.a
        }
    }

    private data class Point(val x: Int, val y: Int) {
        fun isLeftOf(other: Point): Boolean = this.x <= other.x
        fun isRightOf(other: Point): Boolean = this.x >= other.x
        fun isUnderOf(other: Point): Boolean = this.y <= other.y
        fun isAboveOf(other: Point): Boolean = this.y >= other.y
    }

}