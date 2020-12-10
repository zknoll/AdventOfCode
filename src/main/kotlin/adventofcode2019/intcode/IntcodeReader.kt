package adventofcode2019.intcode

import InputReader

class IntcodeReader {
    val status = 0
    fun readFromFile(filepath: String) = InputReader("2019day15_input.txt").fileAsString
        .split(",")
        .map { it.toInt() }
}