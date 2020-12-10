package adventofcode2019.intcode

typealias Program = ArrayList<Int>

class IntComputer(var inputCallback: () -> Int = getInput, var outputCallback: (Int) -> Unit = performPrint) {

    companion object {
        private const val POSITION = "0"
        private const val IMMEDIATE = "1"
        private const val RELATIVE = "2"

        private val performPrint: (Any) -> Unit = { x -> println(x) }
        private val getInput: () -> Int = { readLine()!!.toInt() }
    }

    enum class Operations(val id: Int) {
        ADD(1),
        MULT(2),
        INPUT(3),
        OUTPUT(4),
        JUMP_IF_TRUE(5),
        JUMP_IF_FALSE(6),
        LESSTHAN(7),
        EQUALS(8),
        RELATIVE_BASE_OFFSET(9),
        HALT(99);

        companion object {
            fun getOp(id: Int): Operations? {
                return values().find { it.id == id }
            }
        }
    }

    private val inputParamsMap: HashMap<Operations, Int> = hashMapOf(
        Operations.ADD to 2,
        Operations.MULT to 2,
        Operations.INPUT to 0,
        Operations.OUTPUT to 1,
        Operations.JUMP_IF_TRUE to 2,
        Operations.JUMP_IF_FALSE to 2,
        Operations.LESSTHAN to 2,
        Operations.EQUALS to 2,
        Operations.RELATIVE_BASE_OFFSET to 1,
        Operations.HALT to 0
    )

    private val outputParamsMap: HashMap<Operations, Int> = hashMapOf(
        Operations.ADD to 1,
        Operations.MULT to 1,
        Operations.INPUT to 1,
        Operations.OUTPUT to 0,
        Operations.JUMP_IF_TRUE to 0,
        Operations.JUMP_IF_FALSE to 0,
        Operations.LESSTHAN to 1,
        Operations.EQUALS to 1,
        Operations.RELATIVE_BASE_OFFSET to 0,
        Operations.HALT to 0
    )

    private var ptr = 0
    private var relativeBase = 0
    private var activeProgram: Program = arrayListOf(99)
    var isPaused = false
    var isHalted = false
    private val extendedProgramSpace = hashMapOf<Int, Int>()
    private val operandMap = hashMapOf<Operations, (List<Int>) -> Int?>(
        Operations.ADD to { args -> args[0] + args[1] },
        Operations.MULT to { args -> args[0] * args[1] },
        Operations.INPUT to { inputCallback() },
        Operations.OUTPUT to { args -> outputCallback(args[0]); null },
        Operations.JUMP_IF_TRUE to { args -> if (args[0] != 0) jumpTo(args[1] - 3); null},
        Operations.JUMP_IF_FALSE to { args -> if (args[0] == 0) jumpTo(args[1] - 3); null },
        Operations.LESSTHAN to { args -> if (args[0] < args[1]) 1 else 0 },
        Operations.EQUALS to { args -> if (args[0] == args[1]) 1 else 0 },
        Operations.RELATIVE_BASE_OFFSET to { args -> relativeBaseAdjust(args[0]); null },
        Operations.HALT to { null }
    )

    private fun jumpTo(pos: Int) {
        ptr = pos
    }

    private fun relativeBaseAdjust(value: Int) {
        relativeBase += value
    }

    private fun getOperationType(op: Int): Int {
        val stringOp = op.toString()
        return stringOp.substring((stringOp.length - 2)%stringOp.length until stringOp.length).toInt()
    }

    private fun getOperationModes(op: Int, opType: Int): String {
        var modeStr = op.toString().reversed()
        modeStr = try {
            modeStr.substring(2 until modeStr.length)
        } catch (e: Exception) {
            ""
        }
        //println("$op, $opType")
        while(modeStr.length < inputParamsMap[Operations.getOp(opType)]!! + outputParamsMap[Operations.getOp(opType)]!!) {
            modeStr += "0"
        }
        return modeStr
    }

    private fun getParameterValue(program: Program, operationMode: String, instructionPointer: Int, offset: Int): Int {
        return when (operationMode) {
            POSITION -> { accessModePosition(program, instructionPointer, offset) }
            IMMEDIATE -> { accessModeImmediate(program, instructionPointer, offset) }
            RELATIVE -> { accessModeRelative(program, instructionPointer, offset) }
            else -> throw Exception("Unrecognized operating mode")
        }
    }

    private fun accessModeImmediate(program: Program, instructionPointer: Int, offset: Int) = accessValue(program, instructionPointer + offset)

    private fun accessModePosition(program: Program, instructionPointer: Int, offset: Int) = accessValue(program, accessValue(program, instructionPointer + offset))

    private fun accessModeRelative(program: Program, instructionPointer: Int, offset: Int) = accessValue(program, accessValue(program, instructionPointer + offset) + relativeBase)

    private fun accessValue(program: Program, position: Int): Int {
        return if (position < program.size) {
            program[position]
        } else {
            extendedProgramSpace[position] ?: 0
        }
    }

    private fun writeValue(program: Program, position: Int, value: Int) {
        if (position < program.size) {
            program[position] = value
        } else {
            extendedProgramSpace[position] = value
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
        runProgram(ptr)
    }

    fun installProgram(program: Program) {
        activeProgram = program
    }

    fun runProgram(ptrLoc: Int  = 0) {
        ptr = ptrLoc
        if (isHalted) throw Exception("Program is hallted, please reset to re-run")

        while (!isPaused && !isHalted) {
            val op = activeProgram[ptr]
            val opType = Operations.getOp(getOperationType(op))!!
            if (opType == Operations.HALT) {
                isHalted = true
                operandMap[opType]!!(arrayListOf())
                //println("$op, $opType")
                break
            }
            val opModes = getOperationModes(op, opType.id)
            val args = arrayListOf<Int>()
            for (i in 0 until inputParamsMap[opType]!!) {
                args.add(getParameterValue(activeProgram, opModes[i].toString(), ptr, i+1))
            }
            val resultantValue = operandMap[opType]!!(args)
            //println("$op, <${activeProgram[ptr+1]}, ${activeProgram[ptr+2]}, ${activeProgram[ptr+3]}>, $opType, $opModes, $args, $resultantValue, $ptr")
            if (outputParamsMap[opType] != 0) {
                val offset = inputParamsMap[opType]!! + outputParamsMap[opType]!!
                val outputLocation: Int = when (opModes.last().toString()) {
                    POSITION -> { accessValue(activeProgram, ptr + offset) }
                    RELATIVE -> { accessValue(activeProgram, ptr + offset) + relativeBase }
                    else -> throw Exception("Invalid output parameter mode")
                }
                //println(", $outputLocation")
                //println("Writing value $resultantValue to $outputLocation")
                writeValue(activeProgram, outputLocation, resultantValue!!)
            }
            val positionsToIncrement = inputParamsMap[opType]!! + outputParamsMap[opType]!! + 1
            //print(ptr)
            ptr += positionsToIncrement
            //println(activeProgram)
            //println(", $ptr")
        }
    }

}