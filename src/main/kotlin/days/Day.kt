package days

import InputReader

abstract class Day(filename: String) {
    val inputReader = InputReader(filename)
    val inputLines = inputReader.fileContents
    val testLines = InputReader("test.txt").fileContents
    val testString = InputReader("test.txt").fileAsString
    val inputString = inputReader.fileAsString

    abstract fun part1(): Any
    abstract fun part2(): Any

    fun run() {
        part1()
        part2()
    }
}