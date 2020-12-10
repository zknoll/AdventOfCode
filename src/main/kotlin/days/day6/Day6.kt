package days.day6

import days.Day

class Day6: Day("day6_input.txt") {

    private val conditionedInput = inputString.split("\r\n\r\n").map { it.split("\r\n").map { individual -> individual.toSet() } }

    override fun part1() = println(countAnyUnique(conditionedInput))
    override fun part2() = println(countCommon(conditionedInput))

    private fun countAnyUnique(groupStrings: List<List<Set<Char>>>): Int = groupStrings.map { group ->
            group.reduce { acc, s -> acc.union(s.asIterable()) }.size }.sum()
    private fun countCommon(groupStrings: List<List<Set<Char>>>): Int = groupStrings.map { group ->
            group.reduce { acc, s -> acc.intersect(s.asIterable()) }.size }.sum()

    // cut to one group, the initialize a set and add all chars to it - converting to a sequence was the best way to do this
    // original implementation was on List<String> that had only been split by double line break
    // groupStrings.map { group.replace("\r\n") }.map { group -> setOf().apply { LOWER_ALPHA.toSet() }.intersect(group.asIterable()) }.size
    // for an input of List<List<String>> can also use
    // ---> group.fold(group.first().toSet()) { acc, s -> acc.union(s.asIterable) }.size

    /*private fun countCommon(groupStrings: List<String>): Int {
        // turn it into a List<List<String>>
        return groupStrings.map { groupString ->
            groupString.split("\r\n").also { println(it) }
        }.map { groupList ->
            val counts = HashMap<Char, Int>()
            groupList.forEach { individualAnswers ->
                individualAnswers.forEach { char ->
                    when (val count = counts[char]) {
                        null -> counts[char] = 1
                        else -> counts[char] = count + 1
                    }
                }
            }
            counts.map { charCounts -> if (charCounts.value == groupList.size) 1 else 0 }.sum()
        }.sum()
    }*/
}
