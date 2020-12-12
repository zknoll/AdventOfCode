package days.day12

import days.Day
import kotlin.math.abs

class Day12: Day("day12_input.txt") {
    private val conditionedInput = inputLines.map { Instruction(Direction.find(it.first())!!, magnitude = it.substring(1 until it.length).toInt()) }

    override fun part1() = findDestination(conditionedInput)

    override fun part2() = 0

    private fun findDestination(input: List<Instruction>) {
        val ship = Ship(
            position = Coordinates(0,0),
            heading = Direction.EAST
        )
        input.forEach { ship.followInstruction(it) }
        println(ship.position)
        println(ship.heading)
        println("Manhattan distance = ${abs(ship.position.lateral) + abs(ship.position.vertical)}")
    }

    data class Instruction(val direction: Direction, val magnitude: Int)
    data class Coordinates(val lateral: Int, val vertical: Int)
    data class Ship(var position: Coordinates, var heading: Direction) {
        fun followInstruction(instruction: Instruction) {
            when (instruction.direction) {
                Direction.LEFT,
                Direction.RIGHT -> {
                    heading = rotate(instruction)
                }
                else -> {
                    position = move(instruction,position, heading)
                }
            }
        }
        fun rotate(leftOrRight: Instruction): Direction {
            return when (leftOrRight.direction) {
                Direction.RIGHT -> { when (leftOrRight.magnitude) {
                    90 -> Direction.rotateRight(heading)
                    180 -> {
                        Direction.rotateRight(Direction.rotateRight(heading))
                    }
                    270 -> Direction.rotateLeft(heading)
                    else -> throw Exception("Cannot rotate that amount")
                }}
                Direction.LEFT -> { when (leftOrRight.magnitude) {
                    90 -> Direction.rotateLeft(heading)
                    180 -> {
                        Direction.rotateLeft(Direction.rotateLeft(heading))
                    }
                    270 -> Direction.rotateRight(heading)
                    else -> throw Exception("Cannot rotate that amount")
                }

                }
                else -> throw Exception("Can't rotate with direciton $leftOrRight")
            }
        }
        fun move(direction: Instruction, position: Coordinates, heading: Direction): Coordinates {
            return when (direction.direction) {
                Direction.NORTH -> { Coordinates(position.lateral, position.vertical + direction.magnitude) }
                Direction.SOUTH -> { Coordinates(position.lateral, position.vertical - direction.magnitude) }
                Direction.EAST -> { Coordinates(position.lateral + direction.magnitude, position.vertical) }
                Direction.WEST -> { Coordinates(position.lateral - direction.magnitude, position.vertical) }
                Direction.FORWARD -> { move(Instruction(heading, direction.magnitude), position, heading) }
                else -> throw Exception("Cannot move in a non-cardinal direction or not forward")
            }
        }
    }

    enum class Direction(val id: Char) {
        NORTH('N'),
        EAST('E'),
        WEST('W'),
        SOUTH('S'),
        LEFT('L'),
        RIGHT('R'),
        FORWARD('F');

        companion object {
            fun find(id: Char) = values().find { it.id == id }

            fun rotateLeft(direction: Direction): Direction {
                return when (direction) {
                    NORTH -> WEST
                    WEST -> SOUTH
                    SOUTH -> EAST
                    EAST -> NORTH
                    else -> throw Exception("Cannot rotate a non cardinal direction")
                }
            }

            fun rotateRight(direction: Direction): Direction {
                return when (direction) {
                    NORTH -> EAST
                    EAST -> SOUTH
                    SOUTH -> WEST
                    WEST -> NORTH
                    else -> throw Exception("Cannot rotate a non cardinal direction")
                }
            }
        }
    }
}