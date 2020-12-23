package days.day22

import days.Day
import helpers.pairWith

typealias Deck = List<Int>

class Day22: Day("day22_input.txt") {

    val conditionedInput: List<List<Int>> = inputString.split("\r\n\r\n")
        .map { deck -> deck.split("\r\n").drop(1) }
        .map { cardStrings -> cardStrings.map { it.toInt() } }


    override fun part1() {
        conditionedInput
        val game = CombatGame(conditionedInput[0], conditionedInput[1])
        //println(game.playGame())
    }

    override fun part2() {
        val game = CombatGame(conditionedInput[0], conditionedInput[1], recursive = true)
        println(game.playGame())
    }

    private data class DeckPermutation(val deck1: Deck, val deck2: Deck) {
        override fun equals(other: Any?): Boolean {
            if (other !is DeckPermutation) return false
            // check equality between the two permutations
            return try {
                this.deck1.size == other.deck1.size && this.deck2.size == other.deck2.size &&
                this.deck1.mapIndexed { index, value -> other.deck1[index] == value }
                    .reduce { acc, b -> acc && b } &&
                        this.deck2.mapIndexed { index, value -> other.deck2[index] == value }
                            .reduce { acc, b -> acc && b }
            } catch (e: Exception) {
                false
            }
        }

        override fun hashCode(): Int {
            return this.deck1.hashCode() + this.deck2.hashCode()
        }
    }

    private class CombatGame(val deck1: Deck, val deck2: Deck, val recursive: Boolean = false, val gameId: Int = 0) {
        var winner: Int? = null
        val permutations: HashSet<Int> = hashSetOf()
        val debugFlag = false

        companion object {
            //private val recursiveDecks: HashMap<Int, HashSet<DeckPermutation>> = hashMapOf()
            private val recursiveHashes: HashSet<Int> = hashSetOf()
            //private val recursiveResults: HashMap<Int, Int> = hashMapOf()
        }

        fun debug(str: String) = if (debugFlag) println(str) else Unit
        fun playGame(): Int {
            debug("=== Game $gameId ===")
            var player1 = deck1.map { it }
            var player2 = deck2.map { it }
            var roundCounter = 1
            while (player1.isNotEmpty() && player2.isNotEmpty()) {
                if (gameId == 0 && roundCounter % 100 == 0) {
                    println(" -- Round ${roundCounter} --")
                }
                debug("-- Round ${roundCounter++} --")
                debug("Player 1's deck: $player1")
                debug("Player 2's deck: $player2")
                if (recursive) {
                    val newPermutation = DeckPermutation(player1, player2)
                    //println(permutations.size)
                    if (permutations.contains(newPermutation.hashCode()) || recursiveHashes.contains(newPermutation.hashCode())) {
                        winner = 1
                        recursiveHashes.add(newPermutation.hashCode())
                        recursiveHashes.addAll(permutations)
                        /*if (recursiveDecks.containsKey(newPermutation.deck1.size)) {
                            recursiveDecks[newPermutation.deck1.size]!!.add(newPermutation)
                        } else {
                            recursiveDecks[newPermutation.deck1.size] = hashSetOf(newPermutation)
                        }*/
                        println("Finsihed by infinite loop condition after ${roundCounter - 1} rounds, final deck is $player1")
                        return player1.reversed().reduceIndexed { index, acc, value -> acc + (index + 1) * value }
                    }
                    permutations.add(newPermutation.hashCode())
                }
                val result = playHand(player1, player2)
                player1 = result.first
                player2 = result.second
            }
            val winningHand = if (player1.isNotEmpty()) player1 else player2
            winner = if (player1.isNotEmpty()) 1 else 2
            //println("Finished by domination, final deck is $winningHand")
            return winningHand.reversed().reduceIndexed { index, acc, value -> acc + (index+1)*value }
        }

        private fun playHand(deck1: Deck, deck2: Deck): Pair<List<Int>, List<Int>> {
            val player1 = ArrayList(deck1.map { it })
            val player2 = ArrayList(deck2.map { it })

            val card1 = player1.removeFirst()
            val card2 = player2.removeFirst()

            debug("Player 1 plays: $card1")
            debug("Player 2 plays: $card2")
            if (!recursive) {
                if (card1 > card2) {
                    player1.addAll(listOf(card1, card2))
                } else {
                    player2.addAll(listOf(card2, card1))
                }
            } else {
                if (card1 <= player1.size && card2 <= player2.size) {
                    // new sub-game
                    val subGame = CombatGame(player1.subList(0, card1), player2.subList(0, card2), recursive, gameId + 1)
                    subGame.playGame()
                    debug("Player ${subGame.winner} wins the round!")
                    if (subGame.winner == 1) {
                        player1.addAll(listOf(card1, card2))
                    } else {
                        player2.addAll(listOf(card2, card1))
                    }
                } else {
                    // check normal conditions
                    if (card1 > card2) {
                        player1.addAll(listOf(card1, card2))
                    } else {
                        player2.addAll(listOf(card2, card1))
                    }
                    debug("Player ${if (card1 > card2) 1 else 2} wins the round!")
                }
            }

            return player1 pairWith player2
        }
    }
}