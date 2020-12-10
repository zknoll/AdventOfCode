package adventofcode2019.day7

import adventofcode2019.intcode.Program
import days.Day

class Day7_2019: Day("2019day7_input.txt") {
    private val conditionedInput: Program
        get() = ArrayList(inputString.split(",").map { it.toInt() })
    override fun part1() = findMaxOutput()

    override fun part2() = findMaxOutputWithFeedback()

    private fun findMaxOutput() {
        val outputs = arrayListOf<Int>()
        for (i in 56789..98765) {
            val phase = i.toString().padStart(5, '0')
            if ('4' in phase && '3' in phase && '2' in phase && '1' in phase && '0' in phase) {
                outputs.add(linearOutput(i.toString().padStart(5, '0')))
            }
        }
        println(outputs.maxOrNull())
    }

    private fun findMaxOutputWithFeedback() {
        val outputs = arrayListOf<Int>()
        for (i in 56789..98765) {
            val phase = i.toString().padStart(5, '0')
            if ('5' in phase && '6' in phase && '7' in phase && '8' in phase && '9' in phase) {
                outputs.add(feedbackOutput(i.toString().padStart(5, '0')))
            }
        }
        println(outputs.maxOrNull())
    }

    private fun linearOutput(phase: String): Int {

        val ampA = Amplifier("A", phase[0].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampB = Amplifier("B", phase[1].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampC = Amplifier("C", phase[2].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampD = Amplifier("D", phase[3].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampE = Amplifier("E", phase[4].toInt() - 48).also { it.computer.installProgram(conditionedInput)}

        val bank = AmplifierBank().apply {
            addAmp(ampA)
            addAmp(ampB)
            addAmp(ampC)
            addAmp(ampD)
            addAmp(ampE)
            wireAmpsTogether(ampA.name, ampB.name)
            wireAmpsTogether(ampB.name, ampC.name)
            wireAmpsTogether(ampC.name, ampD.name)
            wireAmpsTogether(ampD.name, ampE.name)
            inputAmp = ampA.name
            outputAmp = ampE.name
            feedbackEnabled = false
        }
        return bank.getOutput()
    }

    private fun feedbackOutput(phase: String): Int {
        val ampA = Amplifier("A", phase[0].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampB = Amplifier("B", phase[1].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampC = Amplifier("C", phase[2].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampD = Amplifier("D", phase[3].toInt() - 48).also { it.computer.installProgram(conditionedInput)}
        val ampE = Amplifier("E", phase[4].toInt() - 48).also { it.computer.installProgram(conditionedInput)}

        val bank = AmplifierBank().apply {
            addAmp(ampA)
            addAmp(ampB)
            addAmp(ampC)
            addAmp(ampD)
            addAmp(ampE)
            wireAmpsTogether(ampA.name, ampB.name)
            wireAmpsTogether(ampB.name, ampC.name)
            wireAmpsTogether(ampC.name, ampD.name)
            wireAmpsTogether(ampD.name, ampE.name)
            wireAmpsTogether(ampE.name, ampA.name)
            inputAmp = ampA.name
            outputAmp = ampE.name
            feedbackEnabled = true
        }
        return bank.getOutput()
    }
}