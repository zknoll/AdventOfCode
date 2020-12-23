package days.day23

import days.Day
import helpers.pairWith
import helpers.toArrayList
import helpers.toLinkedList
import java.util.*
import kotlin.collections.ArrayList

typealias CupSequence = LinkedList<Int>

class Day23: Day("day23_input.txt") {

    val input = "523764819".map { it.toInt() - 48 }.toLinkedList()

    val input2 = "523764819".map { it.toInt() - 48 }.toLinkedList().also {
        for (i in 10..1000000) {
            it.add(i)
        }
    }
    val testInput = "389125467".map { it.toInt() - 48 }.toLinkedList()

    override fun part1() {
        CrabGame(input).playGame(100).also { println(it) }
    }

    override fun part2(){
        val result = CrabGame(input2).playGame(1000000)
        val after1 = result[result.indexOfFirst { it == 1 } + 1]
        val after2 = result[result.indexOfFirst { it == 1 } + 2]
        println(after1.toLong() * after2.toLong())
    }

    private class CrabGame(val input: CupSequence) {
        private val minValue = input.minOrNull()!!.also { println(it) }
        private val maxValue = input.maxOrNull()!!.also { println(it) }

        val debugFlag = false

        fun debug(str: String) = if (debugFlag) println(str) else Unit

        fun playGame(iterations: Int): CupSequence {
            var inputCopy = input.map { it }.toLinkedList()
            var currentValueIndex: Int? = null
            var currentValue: Int? = null
            var moveCounter = 1
            repeat(iterations) {
                currentValueIndex = inputCopy.indexOfNextValue(currentValue)
                currentValue = inputCopy[currentValueIndex!!]
                println("-- move $moveCounter --")
                inputCopy = doMove(currentValueIndex!!, currentValue!!, inputCopy)
                moveCounter++
                println()
            }
            return inputCopy
        }

        fun doMove(currentIndex: Int, currentValue: Int, gameState: CupSequence): CupSequence {
            debug("cups: $gameState")
            debug("value: ${gameState[currentIndex]}")
            //val removedValues = gameState.removeAfterValue(currentValue)
            val removedValues: ArrayList<Int> = arrayListOf()
            var tempIndex = currentIndex
            repeat(3) {
                removedValues.add(gameState.removeAfter(tempIndex))
                val currentIndexValue = try { gameState[currentIndex] } catch (e: Exception) { -1 }
                if (currentIndexValue != currentValue) {
                    tempIndex--
                }
            }
            debug("pick up: $removedValues")
            var targetValue = if (currentValue == minValue) maxValue else currentValue - 1
            while (targetValue in removedValues) {
                targetValue--
                if (targetValue < minValue) {
                    targetValue = maxValue
                }
            }
            debug("destination: $targetValue")
            gameState.insertAfterValue(targetValue, removedValues)
            return gameState
        }

        fun CupSequence.nextValue(currentValue: Int?) = if (currentValue == null) this[0] else this[(indexOfFirst { it == currentValue }.takeUnless { it == -1 }!! + 1) % size]

        fun CupSequence.indexOfNextValue(currentValue: Int?) = if (currentValue == null) 0 else (indexOfFirst { it == currentValue }.takeUnless { it == -1 }!! + 1) % size

        private fun CupSequence.removeAfter(index: Int): Int {
            return removeAt((index + 1) % size)
        }

        private fun CupSequence.insertAfter(index: Int, toInsert: List<Int>): Boolean {
            return this.addAll(index + 1, toInsert)
        }

        private fun CupSequence.removeAfterValue(value: Int): List<Int> {
            val list: ArrayList<Int> = arrayListOf()
            repeat(3) {
                list.add(removeAfter(this.indexOfFirst { it == value }.takeUnless { it == -1 }!!))
            }
            return list
        }

        private fun CupSequence.insertAfterValue(value: Int, toInsert: List<Int>): Boolean = insertAfter(this.indexOfFirst { it == value }.takeUnless { it == -1 }!!, toInsert)
    }

}