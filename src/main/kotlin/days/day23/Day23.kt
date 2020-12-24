package days.day23

import days.Day
import helpers.pairWith
import helpers.toArrayList
import helpers.toLinkedList
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

typealias CupSequence = LinkedList<Int>

class Day23: Day("day23_input.txt") {

    val input = "523764819".map { it.toInt() - 48 }.toLinkedList()
    val testInput = "389125467".map { it.toInt() - 48 }.toLinkedList()

    override fun part1() {
        CrabGame(input).playGame(100).also { println(it) }
    }

    override fun part2(){
        val result = CrabGame2(input, 1000000).playGame(10000000)
        val after1 = result.getEntryByValue(1).next!!
        val after2 = after1.next!!
        //println(result)
        println(after1.value.toLong() * after2.value.toLong())
    }

    private class CrabGame2(input: List<Int>, gameSize: Int) {
        val gameState = CupSequenceTemp(input, gameSize)
        private val minValue = input.minOrNull()!!
        private val maxValue = gameSize

        fun playGame(iterations: Int): CupSequenceTemp {
            //println(gameState)
            var currentEntry: CupSequenceTemp.CupSequenceEntry? = null
            repeat(iterations) {
                currentEntry = if (currentEntry == null) gameState.first else currentEntry!!.next!!
                val nextThree = gameState.getNextThree(currentEntry!!).map { it.value }
                var targetValue = if (currentEntry!!.value == minValue) maxValue else currentEntry!!.value - 1
                while (targetValue in nextThree) {
                    targetValue--
                    if (targetValue < minValue) {
                        targetValue = maxValue
                    }
                }
                gameState.removeThreeAfterAndInsert(currentEntry!!, gameState.getEntryByValue(targetValue))
            }
            return gameState
        }
    }

    private class CrabGame(val input: CupSequence) {
        private val minValue = input.minOrNull()!!
        private val maxValue = input.maxOrNull()!!

        val debugFlag = false

        fun debug(str: String) = if (debugFlag) println(str) else Unit

        fun playGame(iterations: Int): CupSequence {
            var inputCopy = input.map { it }.toLinkedList()
            var currentValueIndex: Int? = null
            var currentValue: Int? = null
            var moveCounter = 1
            repeat(iterations) {
                val time = measureTimeMillis {
                    currentValueIndex = inputCopy.indexOfNextValue(currentValue)
                    currentValue = inputCopy[currentValueIndex!!]
                    if (moveCounter % 1000 == 0) println("-- move $moveCounter --")
                    doMove(currentValueIndex!!, currentValue!!, inputCopy)
                    moveCounter++
                    //println()
                }
                if (moveCounter % 1000 == 0) println(" move took $time ms")
            }
            return inputCopy
        }

        fun doMove(currentIndex: Int, currentValue: Int, gameState: CupSequence): CupSequence {
            //debug("cups: $gameState")
            //debug("value: ${gameState[currentIndex]}")
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
            //debug("pick up: $removedValues")
            var targetValue = if (currentValue == minValue) maxValue else currentValue - 1
            while (targetValue in removedValues) {
                targetValue--
                if (targetValue < minValue) {
                    targetValue = maxValue
                }
            }
            //debug("destination: $targetValue")
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