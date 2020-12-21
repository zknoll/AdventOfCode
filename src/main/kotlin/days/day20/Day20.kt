package days.day20

import days.Day

class Day20: Day("day20_input.txt") {

    companion object {
        private val tileRegex = "Tile (\\d+):\\r\\n((?:[.#]+\\r\\n)+)".toRegex()
    }

    val input = testString

    override fun part1() {
        val tiles = conditionInput(input).also { it.forEach { println(it) } }
    }

    override fun part2() = 0

    private fun conditionInput(input: String): List<Tile> {
        return tileRegex.findAll(input).map { result ->
            Tile(result.groupValues[1].toInt(), result.groupValues[2].split("\r\n"))
        }.toList()
    }

    private class Puzzle(val tiles: List<Tile>) {
        init {

        }
    }

    private data class Tile(val id: Int, val matrix: List<String>) {
        private val borders: ArrayList<String> = arrayListOf()
        init {
            borders.add(matrix.first())
            borders.add(matrix.last())
            borders.add(matrix.map { it.last() }.joinToString(""))
            borders.add(matrix.map { it.first() }.joinToString(""))
            borders.addAll(borders.map { it.reversed() })
        }
    }
}