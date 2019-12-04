package aoc_2019

import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import java.util.stream.Stream

class Day02 {

    private val log = LoggerFactory.getLogger(javaClass)

    fun solve(input : Stream<Int>) : List<Int> {
        val memory = input.collect(Collectors.toList()).toMutableList()
        val program = Program(memory)

        var failsafe = 0
        while (failsafe < 100) {
            val nextCommand = program.readNext()
            if (nextCommand == 99) {
                break
            } else {
                val command = commands[nextCommand]
                if (command == null) {
                    log.info("No command for key {}.", nextCommand)
                    program.readNext()
                } else {
                    command.execute(memory, program)
                }
            }
            failsafe++
        }
        return memory
    }

    private val commands = mapOf(Sum().toPair(), Multiplication().toPair())

    private interface Command {
        fun execute(memory : MutableList<Int>, program : Program)
        fun toPair() : Pair<Int, Command>
    }

    private class Sum : Command {

        private val log = LoggerFactory.getLogger(javaClass)

        override fun execute(memory : MutableList<Int>, program : Program) {
            log.info("Memory before sum: {}.", memory)
            val firstVariable = program.readNext()
            val secondVariable = program.readNext()
            val storeLocation = program.readNext()
            memory[storeLocation] = memory[firstVariable] + memory[secondVariable]
            log.info("Summed element at {} and element at {} then stored it at {}. Memory: {}.", firstVariable, secondVariable, storeLocation, memory)
        }

        override fun toPair(): Pair<Int, Command> {
            return Pair(1, this)
        }
    }

    private class Multiplication : Command {

        private val log = LoggerFactory.getLogger(javaClass)

        override fun execute(memory : MutableList<Int>, program : Program) {
            log.info("Memory before multiplication: {}.", memory)
            val firstVariable = program.readNext()
            val secondVariable = program.readNext()
            val storeLocation = program.readNext()
            memory[storeLocation] = memory[firstVariable] * memory[secondVariable]
            log.info("Multiplied element at {} and element at {} then stored it at {}. Memory: {}.", firstVariable, secondVariable, storeLocation, memory)
        }

        override fun toPair(): Pair<Int, Command> {
            return Pair(2, this)
        }
    }

    private class Program (val memory : List<Int>){
        var index = 0

        fun readNext() : Int {
            return memory[index++]
        }

        fun readAhead() : Int {
            return memory[index]
        }
    }

}


