package aoc_2019

import java.nio.file.Files
import java.nio.file.Paths
import java.text.DecimalFormat

object Aoc2019TestUtils {

    private val format = DecimalFormat("##")

    init {
        format.minimumIntegerDigits = 2
        format.maximumFractionDigits = 2
    }

    fun getInput(day: Int) = Files.readAllLines(getPuzzleInput(day)).stream()

    fun getRawInput(day: Int) = Files.readString(getPuzzleInput(day))

    fun getInputOnLine(day: Int) = getInput(day).flatMap { l -> l.split(",").stream() }.map(String::toInt)

    private fun getPuzzleInput(day: Int) =
        Paths.get(Aoc2019TestUtils::class.java.getResource("puzzle_input_day" + format.format(day) + ".txt").toURI())

}
