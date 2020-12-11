package days.day10

import days.Day
import kotlin.math.abs

class Day10: Day("day10_input.txt") {
    private val conditionedInput = inputLines.map { JoltAdapters(it.toInt()) }

    override fun part1() = findDifferencesOfOneOrThree(conditionedInput)

    override fun part2() = 0

    private fun findDifferencesOfOneOrThree(adapters: List<JoltAdapters>) {
        val sortedAdapters = adapters.sortedBy { it.outputRating }.also { }
        val diffOne = sortedAdapters.foldIndexed(0) { index, acc, next ->
            println("$index, $acc, ${next.outputRating}")
            acc + if (index == 0) {
                if (sortedAdapters[index].outputRating == 1) 1 else 0
            } else {
                if (abs(sortedAdapters[index].outputRating - sortedAdapters[index - 1].outputRating) == 1) 1 else 0
            }
        }
        val diffThree = sortedAdapters.foldIndexed(0) { index, acc, next ->
            println("$index, $acc, ${next.outputRating}")
            acc + if (index == 0) {
                if (sortedAdapters[index].outputRating == 3) 1 else 0
            } else {
                if (abs(sortedAdapters[index].outputRating - sortedAdapters[index-1].outputRating) == 3) 1 else 0
            }
        } + 1
        println("DiffOne = $diffOne, diffThree = $diffThree, product = ${diffOne * diffThree}")
    }

    private class JoltAdapters(val outputRating: Int) {
        val inputRatingMax = outputRating - 1
        val inputRatingMin = outputRating - 3

        fun supportsInput(input: Int) = input in inputRatingMin..inputRatingMax
    }
}