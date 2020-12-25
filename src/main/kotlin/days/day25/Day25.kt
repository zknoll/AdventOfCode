package days.day25

import days.Day

// Don't need to
class Day25: Day("day24_input.txt") {

    val cardPublicKey: Int = 8184785
    val doorPublicKey: Int = 5293040

    override fun part1() {

    }

    override fun part2() {

    }

    private val hackLoopSize(cardKey: Int, doorKey: Int): Pair<Int, Int> {

    }

    private fun transform(subject: Int, loopSize: Int): Int {
        var value = 1
        repeat(loopSize) {
            value *= subject
            value %= 20201227
        }
    }
}