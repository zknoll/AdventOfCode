package days.day20

import days.Day

class Day20: Day("day20_input.txt") {

    companion object {
        private val tileRegex = "Tile (\\d+):\\r\\n((?:[.#]+\\r\\n)+)".toRegex()
    }

    val input = inputString

    override fun part1() {
        val tiles = conditionInput(input).also { it.forEach { println(it) } }
        val puzzle = Puzzle(tiles)
        val cornerTiles = puzzle.tileLinks.filter { it.value.size == 2 }.also {println(it.map { it.key.id }) }
        println(cornerTiles.map { it.key.id.toLong() }.reduce{ acc, l -> acc * l })
    }

    override fun part2() = 0

    private fun conditionInput(input: String): List<Tile> {
        return tileRegex.findAll(input).map { result ->
            Tile(result.groupValues[1].toInt(), result.groupValues[2].trim().split("\r\n"))
        }.toList()
    }

    private class Puzzle(val tiles: List<Tile>) {
        val tileLinks: HashMap<Tile, ArrayList<Tile>> = hashMapOf()
        init {
            tiles.forEach { tile -> tileLinks[tile] = arrayListOf() }
            tiles.forEach { tile ->
                tiles.forEach { other ->
                    if (tile.id != other.id) {
                        if (tile.borders.any { other.borders.contains(it) }) {
                            tileLinks[tile]!!.add(other)
                        }
                    }
                }
            }
        }
    }

    private data class Tile(val id: Int, val matrix: List<String>) {
        val borders: ArrayList<String> = arrayListOf()
        init {
            println(id)
            println(matrix)
            borders.add(matrix.first())
            borders.add(matrix.last())
            borders.add(matrix.map { it.last() }.joinToString(""))
            borders.add(matrix.map { it.first() }.joinToString(""))
            borders.addAll(borders.map { it.reversed() })
        }
    }
}