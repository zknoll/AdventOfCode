package adventofcode2019.day7

import adventofcode2019.intcode.IntComputer

class Amplifier(val name: String, val phase: Int) {
    var inputCount = 0
    var inputVal: Int? = null
    var outputValue: Int? = null

    val inCallback: () -> Int = {
        inputCount++
        if (inputCount == 1) {
            phase
        } else {
            inputVal ?: throw Exception("Amp $name has no input to supply to program")
        }
    }

    val outCallback: (Int) -> Unit = { output ->
        outputValue = output
        computer.pause()
    }

    val computer =  IntComputer(inputCallback = inCallback, outputCallback = outCallback)

    fun setInputCallback(callback: () -> Int) {
        computer.inputCallback = callback
    }

    fun setOutputCallback(callback: (Any) -> Unit) {
        computer.outputCallback = callback
    }

    fun supplyInput(value: Int) {
        inputVal = value
    }


    fun execute() {
        if (computer.isPaused) computer.resume() else computer.runProgram()
    }

    fun hasHalted() = computer.isHalted
}