package aoc_2019

import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import java.util.stream.Stream

class Day01 {

    private val log = LoggerFactory.getLogger(javaClass)

    fun solve(input : Stream<String>) : Long {
        return input
            .map { m -> m.toLong() }
            .map { m -> getFuelToTransport(m) }
            .collect(Collectors.summingLong { l -> l })
    }

    fun solve2(input : Stream<String>) : Long {
        return input
            .map { m -> m.toLong() }
            .map { m -> getFuelToTransportIncludingOwnFuel(m) }
            .collect(Collectors.summingLong { l -> l })
    }

    private fun getFuelToTransport(fuelForFuel: Long): Long {
        val fuel = (fuelForFuel / 3) - 2
        return if (fuel > 0) fuel else 0
    }

    private fun getFuelToTransportIncludingOwnFuel(fuelForFuel: Long): Long {
        val fuel = (fuelForFuel / 3) - 2
        return if (fuel > 0) getFuelToTransportIncludingOwnFuel(fuel) + fuel else 0
    }
}


