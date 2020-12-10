package days.day8

import days.Day
import helpers.pairWith



class Day8: Day("day8_input.txt") {
    private val conditionedInput: Program = ArrayList(inputLines.map { it.split(" ")[0] pairWith it.split(" ")[1].toInt() })
    override fun part1() = Emulator(conditionedInput).apply { executeProgram();   println(accumulator) }
    override fun part2() = Emulator(conditionedInput).apply { decorruptProgram(); println(accumulator) }
}