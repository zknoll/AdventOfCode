package days.day5

import days.Day
import helpers.pairWith
import kotlin.math.pow

class Day5: Day("day5_input.txt") {
    override fun part1() = println("Part 1:  ${inputLines.map { line -> findSeatId(line) }.maxOrNull()}")
    override fun part2() = println("Part 2:  ${findMySeat()}")

    private fun findSeatId(code: String): Int = betterSearchSpace(code)

    private fun findMySeat(): Int {
        val sortedSeats = inputLines.map { line -> findSeatId(line) }.sorted()
        for (i in 0..127*8+7) {
            if (i !in sortedSeats && i-1 in sortedSeats && i+1 in sortedSeats) {
                return i
            }
        }
        return -1
    }

    private fun searchSpace(code: String): Int {
        var range = 0 until 2.0.pow(code.length).toInt()
        for (char in code) {
            when(char) {
                'L', 'F' -> { range = range.takeLower()}
                'R', 'B' -> { range = range.takeUpper()}
            }
        }
        return range.first
    }

    private fun betterSearchSpace(code: String) = code.map { if (it in setOf('R', 'B')) "1" else "0" }.joinToString("").toInt(2)
    //TODO: finish
    private fun betterFindMySeat() = inputLines.map { findSeatId(it) }.sorted()

    private fun IntRange.takeUpper() = ((this.last - this.first)/2 + this.first + 1) .. this.last
    private fun IntRange.takeLower() = this.first .. ((this.last - this.first)/2 + this.first)
    private fun Pair<Int, Int>.getSeatId() = this.first * 8 + this.second

    private fun splitSeatCode(code: String) = code.substring(0 until 7) pairWith code.substring(7 until code.length)
}
