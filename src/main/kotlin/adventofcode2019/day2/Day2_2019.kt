package adventofcode2019.day2

import adventofcode2019.intcode.IntComputer
import adventofcode2019.intcode.Program
import days.Day

class Day2_2019: Day("2019day2_input.txt") {
    private val conditionedInput: Program
        get() = Program(inputString.split(",").map { it.toInt() })
    override fun part1() {
        /*val ic = IntComputer()
        val inputProgram = conditionedInput
        inputProgram[1] = 12
        inputProgram[2] = 2
        ic.installProgram(inputProgram)
        ic.runProgram(0)
        println(inputProgram[0])
        println(inputProgram)*/
    }

    override fun part2() {
        for (noun in 0..99) {
            println("Attempting noun = $noun")
            for (verb in 0..99) {
                val ic = IntComputer()
                val inputProgram = conditionedInput
                inputProgram[1] = noun
                inputProgram[2] = verb
                ic.installProgram(inputProgram)
                ic.runProgram()
                if (inputProgram[0] == 19690720) {
                    println("Noun = $noun, verb = $verb, product = $noun$verb")
                    return
                }
            }
        }
    }
}