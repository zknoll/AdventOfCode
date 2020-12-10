package days.day8

import helpers.pairWith

typealias Program = ArrayList<Pair<String, Int>>
typealias Operation = (Int) -> Unit

class Emulator(private val installedProgram: Program) {
    private var ptr = 0
    private val executedInstructions: HashSet<Int> = hashSetOf()
    var accumulator = 0

    companion object {
        private const val ACC = "acc"
        private const val NOP = "nop"
        private const val JMP = "jmp"
    }

    private val operandMap = hashMapOf<String, Operation>(
        ACC to { value -> accumulator += value },
        NOP to { },
        JMP to { value ->  ptr += value - 1 }
    )

    fun executeProgram(program: Program = installedProgram): Boolean {
        while (!executedInstructions.contains(ptr) && ptr < program.size) {
            executedInstructions.add(ptr)
            executeInstruction(program[ptr])
            ptr++
        }
        return ptr >= program.size
    }

    fun decorruptProgram(): Boolean {
        for (i in installedProgram.indices) {
            reset()
            if (installedProgram[i].first !in setOf(NOP, JMP)) {
                continue
            }
            val program = installedProgram.deepCopy()
            program[i] = (if (program[i].first == NOP) JMP else NOP) pairWith program[i].second
            if (executeProgram(program)) return true
        }
        return false
    }

    private fun reset() {
        ptr = 0
        executedInstructions.clear()
        accumulator = 0
    }

    private fun executeInstruction(instruction: Pair<String, Int>) {
        operandMap[instruction.first]!!(instruction.second)
    }

    private fun Program.deepCopy(): Program = ArrayList(this.map { it.first pairWith it.second })
}