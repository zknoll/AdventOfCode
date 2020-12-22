package days.day22

import days.Day
import helpers.pairWith

class Day22: Day("day22_input.txt") {

    val conditionedInput: List<List<Int>> = inputString.split("\r\n\r\n")
        .map { deck -> deck.split("\r\n").drop(1) }
        .map { cardStrings -> cardStrings.map { it.toInt() } }


    override fun part1() {
        conditionedInput
        val game = CombatGame(conditionedInput[0], conditionedInput[1])
        println(game.playGame())
    }

    override fun part2() {

    }

    private class CombatGame(val deck1: List<Int>, val deck2: List<Int>) {
        fun playGame(): Int {
            var player1 = deck1.map { it }
            var player2 = deck2.map { it }

            while (player1.isNotEmpty() && player2.isNotEmpty()) {
                val result = playHand(player1, player2)
                player1 = result.first
                player2 = result.second
            }
            val winner = if (player1.isNotEmpty()) player1 else player2
            return winner.reversed().reduceIndexed { index, acc, value -> acc + (index+1)*value }
        }

        private fun playHand(deck1: List<Int>, deck2: List<Int>): Pair<List<Int>, List<Int>> {
            val player1 = ArrayList(deck1.map { it })
            val player2 = ArrayList(deck2.map { it })

            val card1 = player1.removeFirst()
            val card2 = player2.removeFirst()

            if (card1 > card2) {
                player1.addAll(listOf(card1, card2))
            } else {
                player2.addAll(listOf(card2, card1))
            }

            return player1 pairWith player2
        }
    }
}