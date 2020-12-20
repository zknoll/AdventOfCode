package days

import InputReader
import kotlin.system.measureTimeMillis

abstract class Day(filename: String) {
    private val inputReader = InputReader(filename)
    val inputLines = inputReader.fileContents
    val testLines = InputReader("test.txt").fileContents
    val testString = InputReader("test.txt").fileAsString
    val inputString = inputReader.fileAsString

    abstract fun part1(): Any
    abstract fun part2(): Any

    fun run() {
        measureTimeMillis {
            part1()
        }.also { println("Part 1 completed in $it ms\r\n\r\n") }
        measureTimeMillis {
            part2()
        }.also { println("Part 2 completed in $it ms\r\n\r\n") }
    }
}