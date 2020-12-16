package days.day12

import days.Day
import kotlin.math.*

class Day12: Day("day12_input.txt") {
    private val conditionedInput = inputLines.map { Instruction(Direction.find(it.first())!!, magnitude = it.substring(1 until it.length).toInt()) }

    override fun part1() = findDestination(conditionedInput)

    override fun part2() = followWaypoint(conditionedInput)

    private fun findDestination(input: List<Instruction>) {
        val ship = Ship(
            position = Coordinates(0,0),
            heading = Direction.EAST,
            relativeWaypoint = Coordinates(lateral = 10, vertical = 1)
        )
        input.forEach { ship.followShipInstruction(it) }
        println(ship.position)
        println(ship.heading)
        println("Manhattan distance = ${abs(ship.position.lateral) + abs(ship.position.vertical)}")
    }

    private fun followWaypoint(input: List<Instruction>) {
        val ship = Ship(
            position = Coordinates(0, 0),
            heading = Direction.EAST,
            relativeWaypoint = Coordinates(lateral = 10, vertical = 1)
        )
        input.forEach { println(it); ship.followWaypointInstructions(it) }
        println(ship.position)
        println(ship.heading)
        println("Manhattan distance = ${abs(ship.position.lateral) + abs(ship.position.vertical)}")
    }

    data class Instruction(val direction: Direction, val magnitude: Int)
    data class Coordinates(val lateral: Int, val vertical: Int)
    data class Ship(var position: Coordinates, var heading: Direction, var relativeWaypoint: Coordinates) {
        fun followShipInstruction(instruction: Instruction) {
            when (instruction.direction) {
                Direction.LEFT,
                Direction.RIGHT -> {
                    heading = rotateShip(instruction)
                }
                else -> {
                    position = moveShip(instruction,position, heading)
                }
            }
        }

        private fun rotateShip(leftOrRight: Instruction): Direction {
            return when (leftOrRight.direction) {
                Direction.RIGHT -> { when (leftOrRight.magnitude) {
                    90 -> Direction.rotateRight(heading)
                    180 -> Direction.rotateRight(Direction.rotateRight(heading))
                    270 -> Direction.rotateLeft(heading)
                    else -> throw Exception("Cannot rotate that amount")
                }}
                Direction.LEFT -> { when (leftOrRight.magnitude) {
                    90 -> Direction.rotateLeft(heading)
                    180 -> Direction.rotateLeft(Direction.rotateLeft(heading))
                    270 -> Direction.rotateRight(heading)
                    else -> throw Exception("Cannot rotate that amount")
                }

                }
                else -> throw Exception("Can't rotate with direciton $leftOrRight")
            }
        }
        private fun moveShip(direction: Instruction, position: Coordinates, heading: Direction): Coordinates {
            return when (direction.direction) {
                Direction.NORTH -> { Coordinates(position.lateral, position.vertical + direction.magnitude) }
                Direction.SOUTH -> { Coordinates(position.lateral, position.vertical - direction.magnitude) }
                Direction.EAST -> { Coordinates(position.lateral + direction.magnitude, position.vertical) }
                Direction.WEST -> { Coordinates(position.lateral - direction.magnitude, position.vertical) }
                Direction.FORWARD -> { moveShip(Instruction(heading, direction.magnitude), position, heading) }
                else -> throw Exception("Cannot move in a non-cardinal direction or not forward")
            }
        }

        fun followWaypointInstructions(instruction: Instruction) {
            when (instruction.direction) {
                Direction.LEFT,
                Direction.RIGHT -> {
                    relativeWaypoint = rotateWaypoint(instruction, relativeWaypoint)
                    println("Moved waypoint to $relativeWaypoint")
                }
                Direction.FORWARD -> {
                    position = moveToWaypoint(instruction.magnitude, position, relativeWaypoint)
                    println("Moved ship to $position")
                }
                else -> {
                    relativeWaypoint = moveWaypoint(instruction, relativeWaypoint)
                    println("Moved waypoint to $relativeWaypoint")
                }
            }
        }

        private fun rotateWaypoint(instruction: Instruction, relativeWaypoint: Coordinates): Coordinates {
            val sign = if (instruction.direction == Direction.LEFT) 1 else -1
            val theta = sign * instruction.magnitude * Math.PI/180
            val x = relativeWaypoint.lateral
            val y = relativeWaypoint.vertical

            return Coordinates(
                lateral =  (x * cos(theta) - y * sin(theta)).roundToInt(),
                vertical = (y * cos(theta) + x * sin(theta)).roundToInt()
            )
        }

        private fun moveToWaypoint(magnitude: Int, position: Coordinates, relativeWaypoint: Coordinates): Coordinates {
            return Coordinates(
                lateral = position.lateral + relativeWaypoint.lateral * magnitude,
                vertical = position.vertical + relativeWaypoint.vertical * magnitude
            )
        }

        private fun moveWaypoint(instruction: Instruction, relativeWaypoint: Coordinates): Coordinates {
            return when (instruction.direction) {
                Direction.NORTH -> { Coordinates(relativeWaypoint.lateral, relativeWaypoint.vertical + instruction.magnitude) }
                Direction.SOUTH -> { Coordinates(relativeWaypoint.lateral, relativeWaypoint.vertical - instruction.magnitude) }
                Direction.EAST -> { Coordinates(relativeWaypoint.lateral + instruction.magnitude, relativeWaypoint.vertical) }
                Direction.WEST -> { Coordinates(relativeWaypoint.lateral - instruction.magnitude, relativeWaypoint.vertical) }
                else -> throw Exception("Cannot move waypoint in a non-cardinal direction or not forward")
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