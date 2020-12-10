package days.day9

import days.Day
import helpers.pairWith

class Day9: Day("day9_input.txt") {
    companion object {
        val preambleSize = 25
    }

    private val input = inputLines.map { it.toLong() }

    override fun part1() = findBrokenCode(input)

    override fun part2() = findWeakness(input)

    private fun findWeakness(input: List<Long>) {
        val brokenCode = findBrokenCode(input)
        for (i in input.indices) {
            var acc = 0L
            var j = i
            while (acc < brokenCode || j >= input.size) {
                acc += input[j++]
            }
            if (acc == brokenCode) {
                val weakness = input.subList(i, j).maxOrNull()!! + input.subList(i,j).minOrNull()!!
                println("Weakness is $weakness")
                println("Range is ${i} to ${j-1}")
                break;
            }
        }
    }

    private fun findBrokenCode(input: List<Long>): Long {
        var preamble = input.subList(0, preambleSize)
        //println(preamble)
        //val nonPreamble = input.subList(preambleSize, input.size)

        var validEntries = arrayListOf<Entry>()
        for (i in 0 until preamble.size) {
            for (j in i+1 until preamble.size) {
                if (preamble[i] == preamble[j]) {
                    break
                }
                val sum = preamble[i] + preamble[j]
                validEntries.find { it.value == sum }?.validPairs?.add(preamble[i] pairWith preamble[j])
                    ?: validEntries.add(Entry(sum, arrayListOf(preamble[i] pairWith preamble[j])))
            }
        }
        // check first value
        if (validEntries.find { it.value == input[preambleSize] } == null) {
            println("it was the first! ${input[preambleSize]}")
        }
        //println("initial valid entries = ${validEntries.map { it.value }}")
        for (i in preambleSize + 1 until input.size) {
            //println("initial valid entries = ${validEntries.map { it.value }}")
            // update the preamble
            val preamble = input.subList(i-preambleSize,i-1)
            val oldValue = input[i-preambleSize - 1]
            val newValue = input[i-1]
            // update validEntries
            // add new
            if (!preamble.contains(newValue)) {
                preamble.forEach {
                    if (it != newValue) {
                        val newValidValue = it + newValue
                        validEntries.find { entry -> entry.value == newValidValue }?.validPairs?.add(newValue pairWith it)
                            ?: validEntries.add(Entry(newValidValue, arrayListOf(newValue pairWith it)))
                        //println("adding $newValidValue, valid entries size = ${validEntries.size}")
                    }
                }
            }
            // drop old, checking if old value not in new preamble

            if (!preamble.contains(oldValue)) {
                // for each entry remove any valid pairs that had the value in it
                validEntries.forEach {
                    it.validPairs = it.validPairs.filter { pair -> !pair.has(oldValue) }
                        .toArrayList() }
                // then remove all entries that no longer have any validPairs
                validEntries = validEntries.filter { it.validPairs.isNotEmpty() }.toArrayList()
                //println("Dropping all values containing $oldValue as a viable parent")
            }
            // then check if value is valid
            if (validEntries.find { it.value == input[i] } == null) {
                println("It was ${input[i]}")
                return input[i]
            }
        }
        return -1
    }

    class Entry(val value: Long, var validPairs: ArrayList<Pair<Long, Long>>)

    fun <T> Pair<T,T>.has(value: T): Boolean = this.first == value || this.second == value
    fun <T> List<T>.toArrayList(): ArrayList<T> = ArrayList<T>(this)
}