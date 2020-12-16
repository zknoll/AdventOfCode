package days.day13

import days.Day
import helpers.pairWith
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class Day13: Day("day13_input.txt") {

    private val earliestTime = inputLines[0].toInt().also { println(it) }
    private val busIds = "\\d+".toRegex().findAll(inputLines[1]).map { it.value.toInt() }.toList()

    private val busSchedule = inputLines[1]

    override fun part1() = findOptimalBusPart1(busIds, earliestTime)

    override fun part2() = findPart2(busSchedule)

    private fun findOptimalBusPart1(busIds: List<Int>, earliestTime: Int) {
        val waitTime = busIds.map { it - earliestTime % it }
        val minWaitTime = waitTime.minOrNull()!!
        println(minWaitTime)
        println(busIds[waitTime.indexOfFirst { it == minWaitTime }] * minWaitTime)
        println(busIds[waitTime.indexOfFirst { it == minWaitTime }])
    }

    private fun findPart2(busSchedule: String) {
        val buses = "\\d+".toRegex().findAll(busSchedule).map { it.value.toInt() }.toList()
        val busWithConstraints = buses.map { bus -> bus pairWith busSchedule.split(",").indexOfFirst { it == bus.toString() } }
        println(busWithConstraints)

        var timeIncrement: Long = busWithConstraints[0].first.toLong()
        var time: Long = timeIncrement
        var matchingBuses = 1
        val timeInMillis = measureNanoTime {
            while (true) {
                var matchingCount = 0
                for (i in 0 .. matchingBuses) {
                    if ((time + busWithConstraints[i].second) % busWithConstraints[i].first == 0L) {
                        matchingCount++
                    }
                }
                if (matchingCount > matchingBuses) {
                    println("Found matching count = $matchingCount, at time $time")
                    println("updating time increment to ${timeIncrement * busWithConstraints[matchingCount-1].first}")
                    if (matchingCount == busWithConstraints.size) {
                        break
                    }
                    matchingBuses = matchingCount
                    timeIncrement *= busWithConstraints[matchingBuses - 1].first
                } else if (matchingCount > 1) {
                    //println("Found matching count = $matchingCount")
                }
                time += timeIncrement
                //println(time)
            }
        }
        println(time)
        println(timeIncrement)
        println(timeInMillis)
    }
}