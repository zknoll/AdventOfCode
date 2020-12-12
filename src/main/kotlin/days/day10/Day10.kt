package days.day10

import days.Day
import helpers.pairWith
import kotlin.math.abs
import kotlin.math.min

class Day10: Day("day10_input.txt") {
    private val conditionedInput = ArrayList(testLines.map { JoltAdapter(it.toInt()) }).also {
        it.add(JoltAdapter(0))
        it.add(JoltAdapter(it.maxOf { it.outputRating } + 3))
    }.sortedBy { it.outputRating }

    override fun part1() = findDifferencesOfOneOrThree(conditionedInput)

    override fun part2() = findDifferentConfigurations(conditionedInput)

    private fun findDifferencesOfOneOrThree(adapters: List<JoltAdapter>) {
        val diffOne = adapters.foldIndexed(0) { index, acc, next ->
            acc + if (index == 0) {
                if (adapters[index].outputRating == 1) 1 else 0
            } else {
                if (abs(adapters[index].outputRating - adapters[index - 1].outputRating) == 1) 1 else 0
            }
        }
        val diffThree = adapters.foldIndexed(0) { index, acc, next ->
            acc + if (index == 0) {
                if (adapters[index].outputRating == 3) 1 else 0
            } else {
                if (abs(adapters[index].outputRating - adapters[index-1].outputRating) == 3) 1 else 0
            }
        } + 1
        println("DiffOne = $diffOne, diffThree = $diffThree, product = ${diffOne * diffThree}")
    }

    private fun findDifferentConfigurations(adapters: List<JoltAdapter>) {
        val discardable = removeLockedRows(adapters)
        println(discardable.size)
        println(discardable.map { it.outputRating })

        val binomialSum = discardable.mapIndexed { index, _ ->  binomial(discardable.size, index) }.sum()
        println("binomial sum + 1 = $binomialSum")

        var triosBinomialSum = 0L
        val discardablePairsAndTriosCount = countNonDiscardablePairsAndTrios(adapters, discardable)
        for (i in 0..discardable.size - 3) {
            triosBinomialSum += binomial(discardable.size - 3, i)
        }
        println("triosBinomialSum = $triosBinomialSum")
        println("There were this many of those that produced invalid ones:  ${discardablePairsAndTriosCount.second * triosBinomialSum}")
        println("diff = ${binomialSum - discardablePairsAndTriosCount.second * triosBinomialSum}")
        println("but this counts a lot of duplicates")




    }

    private fun removeLockedRows(adapters: List<JoltAdapter>): List<JoltAdapter> {
        val discardableAdapters = arrayListOf<JoltAdapter>()
        for (i in adapters.indices) {
            if (i == 0 || i == adapters.size - 1) {
                continue
            }
            //println("i = ${adapters[i].outputRating}, i-1 = ${adapters[i-1].outputRating}, i+1 = ${adapters[i+1].outputRating}")
            if (adapters[i].outputRating - adapters[i-1].outputRating >= 3) {
                continue
            }
            if (adapters[i+1].outputRating - adapters[i-1].outputRating >= 3) {
                continue
            }
            discardableAdapters.add(adapters[i])
        }
        return discardableAdapters
    }

    private fun countNonDiscardablePairsAndTrios(adapters: List<JoltAdapter>, discardable: List<JoltAdapter>): Pair<Int, Int> {
        var countPairs = 0
        var countTrios = 0
        var i = 1
        while (i in 1 until adapters.size - 2) {
            if (adapters[i] in discardable) {
                // check if next is in discardable as well
                if (adapters[i + 1] in discardable) {
                    println("${adapters[i+2].outputRating}, ${adapters[i-1].outputRating}")
                    // great, check if we can remove both of them
                    if (!adapters[i+2].supportsInput(adapters[i-1].outputRating)) {
                        println("Found nonDiscardable pair, ${adapters[i].outputRating pairWith adapters[i+1].outputRating}")
                        countPairs++
                        i += 2
                        continue
                    } else if (adapters[i+2] in discardable) {
                        println("Found nonDiscardable trio, ${adapters[i].outputRating}, ${adapters[i+1].outputRating}, ${adapters[i+2].outputRating}")
                        countTrios++
                        i += 3
                    }
                }
            }
            // step to next
            i++
        }
        return countPairs pairWith countTrios
    }

    private class JoltAdapter(val outputRating: Int) {
        val inputRatingMax = outputRating - 1
        val inputRatingMin = outputRating - 3

        fun supportsInput(input: Int) = input in inputRatingMin..inputRatingMax
    }

    fun binomial(n: Int, k: Int) = when {
        n < 0 || k < 0 -> throw IllegalArgumentException("negative numbers not allowed")
        n == k         -> 1L
        else           -> {
            val kReduced = min(k, n - k)    // minimize number of steps
            var result = 1L
            var numerator = n
            var denominator = 1
            while (denominator <= kReduced)
                result = result * numerator-- / denominator++
            result
        }
    }
}