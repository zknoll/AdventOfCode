package days.day14

import days.Day
import java.math.BigInteger
import java.math.BigInteger.ZERO
import kotlin.math.pow
import kotlin.math.roundToInt

class Day14: Day("day14_input.txt") {

    companion object {
        val maskRegex = "^mask\\s=\\s\\w+\$".toRegex()
        val memRegex = "^mem\\[(\\d+)\\] = (\\d+)\$".toRegex()
    }

    val conditionedInput = inputLines
    override fun part1() = runProgramPart1(conditionedInput)

    override fun part2() = runProgramPart2(conditionedInput)

    private fun runProgramPart1(input: List<String>) {
        val memory = hashMapOf<Int, BigInteger>()
        lateinit var bitmask: BitMask
        for (line in input) {
            if (maskRegex.find(line) != null) {
                bitmask = updateBitMask(line.split(" ")[2].trim())
                //println(bitmask)
            } else {
                memRegex.find(line)?.groupValues?.also {
                    val location = it[1].toInt()
                    val value = BigInteger.valueOf(it[2].toLong())
                    //println("Masking value $value")
                    //println("Ones masked value = ${value.or(bitmask.onesMask)}, zeroMasked value = ${value.and(bitmask.zerosMask)}")
                    memory[location] = value.or(bitmask.onesMask).and(bitmask.zerosMask)
                    //println(memory[location])
                }
            }
        }
        println(memory.map { it }.fold(BigInteger.valueOf(0)) { acc, entry -> acc + entry.value } )
    }

    fun runProgramPart2(input: List<String>) {
        val memory = hashMapOf<BigInteger, BigInteger>()
        lateinit var bitmask: BitMask
        val sum = BigInteger.valueOf(0)
        for (line in input) {
            if (maskRegex.find(line) != null) {
                bitmask = updateBitMask(line.split(" ")[2].trim())
                println(bitmask)
            } else {
                memRegex.find(line)?.groupValues?.also {
                    val location = BigInteger.valueOf(it[1].toLong())
                    val value = BigInteger.valueOf(it[2].toLong())
                    println("Masking memory location $location")
                    val startingAddress = location.or(bitmask.onesMask).and(bitmask.floatMask)
                    val memoryAddressesModified = collectMemoryAddresses(startingAddress, bitmask.floatPositions, 0)
                    //println(memoryAddressesModified)
                    memoryAddressesModified.forEach { loc -> memory[loc] = value }
                }
            }
        }
        println(memory.map { it }.fold(BigInteger.valueOf(0)) { acc, entry -> acc + entry.value } )
    }

    fun collectMemoryAddresses(startingAddress: BigInteger, floatPositions: List<Int>, position: Int): HashSet<BigInteger> {
        // position is position in the list, floatMask has highest position first, which is convenient
        val floatPosition = BigInteger.valueOf(2).pow(floatPositions[position])
        val listOfPositions = hashSetOf<BigInteger>()
        if (position < floatPositions.size - 1) {
            listOfPositions.addAll(collectMemoryAddresses(startingAddress, floatPositions, position + 1))
            listOfPositions.addAll(collectMemoryAddresses(startingAddress.add(floatPosition), floatPositions, position + 1))
        } else {
            //println("Collecting memory addresses for $startingAddress, and ${startingAddress.add(floatPosition)}")
            listOfPositions.add(startingAddress)
            listOfPositions.add(startingAddress.add(floatPosition))
        }
        return listOfPositions

    }

    fun updateBitMask(maskRaw: String): BitMask {
        //println(maskRaw)
        val onesMaskPositions = "1".toRegex().findAll(maskRaw).map { maskRaw.length - it.range.first - 1 }.toList()
        val zerosMaskPositions = "0".toRegex().findAll(maskRaw).map { maskRaw.length - it.range.first - 1 }.toList()
        val floatMaskPositions = "X".toRegex().findAll(maskRaw).map { maskRaw.length - it.range.first - 1 }.toList()
        //println(floatMaskPositions)
        var onesMask = BigInteger.valueOf(0)
        var zerosMask = BigInteger.valueOf(0)
        var floatMask = BigInteger.valueOf(0)
        onesMaskPositions.forEach { onesMask += BigInteger.valueOf(1).shiftLeft(it) }
        zerosMaskPositions.forEach { zerosMask += BigInteger.valueOf(1).shiftLeft(it) }
        floatMaskPositions.forEach { floatMask += BigInteger.valueOf(1).shiftLeft(it) }
        return BitMask(onesMask, zerosMask.inv(), floatMask.inv(), floatMaskPositions)
    }

    data class BitMask(val onesMask: BigInteger, val zerosMask: BigInteger, val floatMask: BigInteger, val floatPositions: List<Int>) {
        override fun toString(): String {
            return "BitMask(onesMask = ${Integer.toBinaryString(onesMask.toInt())}, " +
                    "zerosMask = ${Integer.toBinaryString(zerosMask.toInt())}, " +
                    "floatMask = $floatMask"
        }
    }
}