package days.day15

import days.Day
import helpers.pairWith
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class Day15: Day("day15_input.txt") {
    val conditionedInput = inputString.split(",").map { it.trim().toInt() }
    override fun part1() = findNumberInPosition(conditionedInput, 2020)

    override fun part2() = println( measureTimeMillis { findNumberInPosition(conditionedInput, 30000000) } / 1000 )

    private fun findNumberInPosition(startingNumbers: List<Int>, position: Int) {
        val numbersSaid: ArrayList<Int> = arrayListOf()
        val lastPositionMap: HashMap<Int, Pair<Int, Int?>> = hashMapOf()

        for (num in startingNumbers) {
            numbersSaid.add(num)
            lastPositionMap[num] = numbersSaid.size pairWith null
        }
        for (i in numbersSaid.size until position) {
            lastPositionMap[numbersSaid[i-1]]!!.second?.also {
                val pair = lastPositionMap[numbersSaid[i-1]]!!
                val newNumber = pair.first - it
                numbersSaid.add(newNumber)
                lastPositionMap[newNumber] = numbersSaid.size pairWith lastPositionMap[newNumber]?.first
            } ?: run {
                numbersSaid.add(0)
                lastPositionMap[0] = numbersSaid.size pairWith lastPositionMap[0]?.first
            }
        }
        println(numbersSaid.last())
    }
}