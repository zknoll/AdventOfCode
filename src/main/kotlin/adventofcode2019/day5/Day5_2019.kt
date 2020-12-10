package adventofcode2019.day5

import adventofcode2019.intcode.IntComputer
import days.Day

class Day5_2019: Day("2019Day5_input.txt") {
    val conditionedInput = ArrayList(inputString.split(",").map { it.toInt() })
    override fun part1() {
        val ic = IntComputer()
        ic.installProgram(conditionedInput)
        ic.runProgram()
        //provide input 1
    }

    override fun part2() {
        val ic = IntComputer()
        ic.installProgram(conditionedInput)
        ic.runProgram()
        //provide input 5
    }
}