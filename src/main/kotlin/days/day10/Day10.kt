package days.day10

import days.Day
import helpers.pairWith
import kotlin.math.*

class Day10: Day("day10_input.txt") {
    private val conditionedInput = ArrayList(inputLines.map { JoltAdapter(it.toInt()) }).also {
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

    private fun findDifferentConfigurationsWithMap(adapters: List<JoltAdapter>) {
        val discardable = removeLockedRows(adapters)
        val binomialSum = (0..discardable.size).map { k -> binomial(discardable.size, k) }.sum()
        val discardablePairsAndTriosCount = countNonDiscardablePairsAndTrios(adapters, discardable)
        val nonDiscardableTrios = discardablePairsAndTriosCount.second

        val D = discardable.size
        val G = nonDiscardableTrios
        IntRange
        val invalidSets = (0 until G).map { k ->
            (0..k).map { n ->
                (0..k).map { m ->
                    if (n + m <= k) binomial(k, n+m) * binomial(n+m, m) *
                            binomial(3, 0).pow(k-n-m) * binomial(3,1).pow(m) * binomial(3,2).pow(n) else 0
                }
            }.flatten().sum() * (0..(D-3*(k+1))).map { l -> binomial(D - 3*(k+1), l) }.sum()
        }.sum()

        println("Found $invalidSets invalid Sets")
        println("Total = ${binomialSum - invalidSets}")
    }

    private fun findDifferentConfigurations(adapters: List<JoltAdapter>) {
        val discardable = removeLockedRows(adapters)
        println("Found ${discardable.size} discardable adapters")

        var binomialSum = 0L
        for (i in 0 .. discardable.size) {
            binomialSum += binomial(discardable.size, i)
        }
        println("binomial sum = $binomialSum")

        val discardablePairsAndTriosCount = countNonDiscardablePairsAndTrios(adapters, discardable)
        val nonDiscardableTrios = discardablePairsAndTriosCount.second
        println("There are $nonDiscardableTrios non-discardable trios")
        val D = discardable.size
        val G = nonDiscardableTrios
        var invalidSets = 0L
        for (k in 0 until G) {
            var coeff = 0L
            for (n in 0 .. k) {
                for (m in 0 .. k) {
                    if (n + m <= k) {
                        coeff += binomial(k, n+m) * binomial(n+m, m) *
                                binomial(3, 0).pow(k-n-m) * binomial(3,1).pow(m) * binomial(3,2).pow(n)
                    }
                }
            }
            var unscaledSum = 0L
            for (l in 0 .. (D - 3*(k+1))) {
                unscaledSum += binomial(D - 3*(k+1), l)
            }
            invalidSets += coeff * unscaledSum
        }
        println("Found $invalidSets invalid Sets")
        println("Total = ${binomialSum - invalidSets}")
    }

    private fun removeLockedRows(adapters: List<JoltAdapter>): List<JoltAdapter> {
        val discardableAdapters = arrayListOf<JoltAdapter>()
        for (i in adapters.indices) {
            if (i == 0 || i == adapters.size - 1) {
                continue
            }
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

    private fun binomial(n: Int, k: Int) = when {
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

    private fun Long.pow(exp: Int): Long {
        return this.toDouble().pow(exp).roundToLong()
    }
}