package aoc_2019

import java.nio.file.Files
import java.nio.file.Paths
import java.text.DecimalFormat
import java.util.stream.Stream

object Aoc2019TestUtils {

    var format = DecimalFormat("##")

    init {
        format.minimumIntegerDigits = 2
        format.maximumFractionDigits = 2
    }


    fun getInput(name: String?): Stream<String> {
        return Files.readAllLines(Paths.get(Aoc2019TestUtils::class.java.getResource(name).toURI()))
            .stream()
    }

    fun getInput(day: Int): Stream<String> {

        val s = "puzzle_input_day" + format.format(day) + ".txt"
        return Files.readAllLines(Paths.get(Aoc2019TestUtils::class.java.getResource(s).toURI()))
            .stream()
    }

    fun getInputOnLine(day: Int): Stream<Int> {
        return getInput(day).flatMap{ l -> l.split(",").stream() }.map(String::toInt)
    }
}
