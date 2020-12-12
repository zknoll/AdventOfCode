package days.day11

import days.Day
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

class Day11: Day("day11_input.txt") {
    companion object {
        val seatSet = setOf('#', 'L')
    }

    val conditionedInput = inputLines.map { it.toCharArray() }

    override fun part1() = 0//findStableSeatingPart1(conditionedInput)

    override fun part2(): Any = findStableSeatingPart2(conditionedInput)

    private fun findStableSeatingPart1(input: List<CharArray>) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var currentBoard = input
        while (true) {
            val seatCounts = getAdjacentFilledSeatCounts(currentBoard)
            val newBoard = applySocialRules(currentBoard, seatCounts)
            seatCounts.forEach { println(it) }
            println()
            newBoard.forEach { println(it) }
            println()
            if (currentBoard.deepEquals(newBoard)) {
                break
            }
            currentBoard = newBoard
        }
        println(currentBoard.map { it.count { c -> c == '#' } }.sum())

    }

    private fun findStableSeatingPart2(input: List<CharArray>) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var currentBoard = input
        while (true) {
            val seatCounts = getFilledSeatsInLineOfSight(currentBoard)
            val newBoard = applySocialRulesPart2(currentBoard, seatCounts)
            seatCounts.forEach { println(it) }
            println()
            newBoard.forEach { println(it) }
            println()
            if (currentBoard.deepEquals(newBoard)) {
                break
            }
            currentBoard = newBoard
        }
        println(currentBoard.map { it.count { c -> c == '#' } }.sum())

    }

    private fun applySocialRules(seatStatuses: List<CharArray>, adjacentCounts: ArrayList<ArrayList<Int>>): List<CharArray> {
        val newBoard: ArrayList<CharArray> = ArrayList(seatStatuses.map { array -> array.map { it }.toCharArray() })
        seatStatuses.forEachIndexed { i, chars ->
            chars.forEachIndexed { j, c ->
                if (c in setOf('#', 'L')) {
                    if (c == 'L' && adjacentCounts[i][j] == 0) {
                        newBoard[i][j] = '#'
                    } else if (c == '#' && adjacentCounts[i][j] >= 4) {
                        newBoard[i][j] = 'L'
                    }
                }
            }
        }
        return newBoard
    }

    fun getAdjacentFilledSeatCounts(seatStatuses: List<CharArray>): ArrayList<ArrayList<Int>> {
        val seatCounts: ArrayList<ArrayList<Int>> = arrayListOf()

        // iterate over board
        for (i in seatStatuses.indices) {
            seatCounts.add(arrayListOf())
            for(j in seatStatuses[0].indices) {
                if (seatStatuses[i][j] !in setOf('L','#')) {
                    seatCounts[i].add(-1)
                    continue
                }
                var count = 0
                for (k in max(i-1, 0)..min(i+1,seatStatuses.size-1)) {
                    for (m in max(j-1, 0)..min(j+1, seatStatuses[0].size-1)) {
                        if (k != i || m != j) {
                            count += if (seatStatuses[k][m] == '#') 1 else 0
                        }
                    }
                }
                seatCounts[i].add(count)
            }
        }
        return seatCounts
    }

    private fun applySocialRulesPart2(seatStatuses: List<CharArray>, adjacentCounts: ArrayList<ArrayList<Int>>): List<CharArray> {
        val newBoard: ArrayList<CharArray> = ArrayList(seatStatuses.map { array -> array.map { it }.toCharArray() })
        seatStatuses.forEachIndexed { i, chars ->
            chars.forEachIndexed { j, c ->
                if (c in setOf('#', 'L')) {
                    if (c == 'L' && adjacentCounts[i][j] == 0) {
                        newBoard[i][j] = '#'
                    } else if (c == '#' && adjacentCounts[i][j] >= 5) {
                        newBoard[i][j] = 'L'
                    }
                }
            }
        }
        return newBoard
    }

    fun getFilledSeatsInLineOfSight(seatStatuses: List<CharArray>): ArrayList<ArrayList<Int>> {
        val seatCounts: ArrayList<ArrayList<Int>> = arrayListOf()

        // iterate over board
        for (i in seatStatuses.indices) {
            seatCounts.add(arrayListOf())
            for(j in seatStatuses[0].indices) {
                if (seatStatuses[i][j] !in seatSet) {
                    seatCounts[i].add(-1)
                    continue
                }
                var count = 0
                //check left and right
                //println("Checking position ($i, $j)")
                for (k in 1 until seatStatuses[i].size) {
                    if (j - k >= 0 && seatStatuses[i][j-k] in seatSet) {
                        count += if (seatStatuses[i][j-k] == '#') 1 else 0
                        break;
                    }
                }
                for (k in j until seatStatuses[i].size) {
                    if (k != j && seatStatuses[i][k] in seatSet) {
                        count += if (seatStatuses[i][k] == '#') 1 else 0
                        break
                    }
                }
                // check up and down
                for (k in 1 until seatStatuses.size) {
                    if (i - k >= 0 && seatStatuses[i-k][j] in seatSet) {
                        count += if (seatStatuses[i-k][j] == '#') 1 else 0
                        break
                    }
                }
                for (k in i until seatStatuses.size) {
                    if (k != i && seatStatuses[k][j] in seatSet) {
                        count += if (seatStatuses[k][j] == '#') 1 else 0
                        break
                    }
                }
                // check diagonals
                for (k in 1 .. seatStatuses.size) {
                    if (i - k >= 0 && j - k >= 0 && seatStatuses[i-k][j-k] in seatSet) {
                        count += if (seatStatuses[i-k][j-k] == '#') 1 else 0
                        break
                    }
                }
                for (k in 1 .. seatStatuses.size) {
                    if (i + k < seatStatuses.size && j + k < seatStatuses[0].size && seatStatuses[i+k][j+k] in seatSet) {
                        count += if (seatStatuses[i+k][j+k] == '#') 1 else 0
                        break
                    }
                }
                for (k in 1 .. seatStatuses.size) {
                    if (i - k >= 0 && j + k < seatStatuses[0].size && seatStatuses[i-k][j+k] in seatSet) {
                        count += if (seatStatuses[i-k][j+k] == '#') 1 else 0
                        break
                    }
                }
                for (k in 1 .. seatStatuses.size) {
                    if (i + k < seatStatuses.size && j - k >= 0 && seatStatuses[i+k][j-k] in seatSet) {
                        count += if (seatStatuses[i+k][j-k] == '#') 1 else 0
                        break
                    }
                }
                seatCounts[i].add(count)
            }
        }
        return seatCounts
    }

    private fun List<CharArray>.deepCopy() = this.map { array ->  array.map { it } }
    private fun List<CharArray>.deepEquals(other: List<CharArray>) = if (this.size == other.size) {
        this.mapIndexed { index, chars -> chars.contentEquals(other[index])}.reduce { acc, b -> acc && b}
    } else {
        false
    }
}