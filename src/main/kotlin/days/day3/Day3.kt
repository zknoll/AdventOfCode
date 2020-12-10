package days.day3

import days.Day

class Day3: Day("day3_input.txt") {

    @kotlin.ExperimentalUnsignedTypes
    override fun part1() {
        val count1 = countTreesForGivenSlope(inputLines, 1, 1)
        val count2 = countTreesForGivenSlope(inputLines, 3, 1)
        val count3 = countTreesForGivenSlope(inputLines, 5, 1)
        val count4 = countTreesForGivenSlope(inputLines, 7, 1)
        val count5 = countTreesForGivenSlope(inputLines, 1, 2)
        println(arrayListOf(Pair(1,1), Pair(3,1), Pair(5,1), Pair(7,1), Pair(1,2)).map {entry ->  countTreesForGivenSlope(inputLines, entry.first, entry.second).toLong() }.reduce{ acc, value -> acc * value })
        println((count1 * count2 * count3 * count4 * count5).toUInt())
    }

    override fun part2() { /* reimplemented in part 1*/ }

    //x = right, y = down
    private fun countTreesForGivenSlope(input: List<String>, xSlope: Int, ySlope: Int): Int {
        var count = 0
        var j = 0
        repeat(input.size / ySlope) { i -> if (input[ySlope*i][(xSlope*j++) % input[0].length].toString() == "#") { count++ } }
        return count
    }
}