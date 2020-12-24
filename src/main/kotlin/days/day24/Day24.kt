package days.day24

import days.Day
import helpers.pairWith
import helpers.toArrayList

class Day24: Day("day24_input.txt") {

    private fun <T> Collection<T>.printEach() = this.forEach { println(it) }

    private val conditionedInput = conditionInput(inputLines.map { it.trim() })

    override fun part1() {
        conditionedInput.printEach()
        println()
        findFlippedTiles(conditionedInput).also { it.printEach(); println(it.size) }
    }

    override fun part2() {
        val day1Tiles = findFlippedTiles(conditionedInput)
        println("Day 1 has ${day1Tiles.size} tiles")
        iterateTileLayouts(day1Tiles, 100)
    }

    private fun iterateTileLayouts(day1Tiles: List<HexCoords>, dayToIterateTo: Int) {
        val prevBlackTiles = day1Tiles.toArrayList()
        repeat(dayToIterateTo) {
            val tilesToRemove: HashSet<HexCoords> = hashSetOf()
            val tilesToAdd: HashSet<HexCoords> = hashSetOf()

            prevBlackTiles.forEach { blackTile ->
                val adjacentBlackTileCount = blackTile.getAdjacentTileCoords().count { adj ->
                    prevBlackTiles.contains(adj)
                }
                if (adjacentBlackTileCount == 0 || adjacentBlackTileCount > 2) {
                    //println("Removing tile at position $blackTile")
                    tilesToRemove.add(blackTile)
                }
                // get adjacent white tiles
                val adjacentWhiteTiles = blackTile.getAdjacentTileCoords().filter { !prevBlackTiles.contains(it) }
                adjacentWhiteTiles.forEach { whiteTile ->
                    if (whiteTile.getAdjacentTileCoords().count { prevBlackTiles.contains(it) } == 2) {
                        //println("Adding tile at position $whiteTile")
                        tilesToAdd.add(whiteTile)
                    }
                }
            }
            prevBlackTiles.removeAll(tilesToRemove)
            prevBlackTiles.addAll(tilesToAdd)
            println(prevBlackTiles.size)
        }
    }

    private fun findFlippedTiles(input: List<List<HexDir>>): List<HexCoords> {
        val orderedInput = input.map { orderDirections(it) }
        val listOfBlackTiles: ArrayList<HexCoords> = arrayListOf()

        orderedInput.forEach {
            val coord = generateHexCoord(it)
            if (listOfBlackTiles.contains(coord)) {
                listOfBlackTiles.remove(coord)
            } else {
                listOfBlackTiles.add(coord)
            }
        }
        return listOfBlackTiles
    }

    private fun orderDirections(input: List<HexDir>) = input.sortedBy { it.ordinal }

    private fun conditionInput(input: List<String>): List<List<HexDir>> {
        val tileDirs: ArrayList<ArrayList<HexDir>> = arrayListOf()
        input.forEach { tileLocation ->
            val iterator = tileLocation.iterator()
            val nextDir: ArrayList<HexDir> = arrayListOf()
            while(iterator.hasNext()) {

                val value = iterator.next()
                when (value) {
                    'e' -> nextDir.add(HexDir.E)
                    'w' -> nextDir.add(HexDir.W)
                    'n','s' -> {
                        val finishingValue = iterator.next()
                        when (finishingValue) {
                            'e' -> { if (value == 'n') nextDir.add(HexDir.NE) else nextDir.add(HexDir.SE) }
                            'w' -> { if (value == 'n') nextDir.add(HexDir.NW) else nextDir.add(HexDir.SW) }
                            else -> throw Exception()
                        }
                    }
                    else -> throw Exception("Value $value is not valid")
                }
            }
            tileDirs.add(nextDir)
        }
        return tileDirs
    }

    private enum class HexDir(val dirStr: String) {
        E("e"),
        W("w"),
        NE("ne"),
        NW("nw"),
        SE("se"),
        SW("sw");
    }

    private fun generateHexCoord(dirs: List<HexDir>): HexCoords {
        var xTemp: Int = 0
        var yTemp: Int = 0
        dirs.forEach {
            when (it) {
                HexDir.E -> xTemp += 2
                HexDir.W -> xTemp -= 2
                HexDir.NE -> { yTemp += 2; xTemp += 1 }
                HexDir.NW -> { yTemp += 2; xTemp -= 1 }
                HexDir.SE -> { yTemp -= 2; xTemp += 1 }
                HexDir.SW -> { yTemp -= 2; xTemp -= 1 }
            }
        }
        return HexCoords(xTemp, yTemp)
    }

    private class HexCoords(val x: Int, val y: Int) {
        fun getAdjacentTileCoords(): ArrayList<HexCoords> {
            return arrayListOf(
                HexCoords(x-2, y),
                HexCoords(x+2, y),
                HexCoords(x-1, y-2),
                HexCoords(x-1, y+2),
                HexCoords(x+1, y-2),
                HexCoords(x+1, y+2)
            )
        }

        override fun equals(other: Any?): Boolean {
            if (other !is HexCoords) return false
            return this.x == other.x && this.y == other.y
        }

        override fun toString(): String {
            return "HexCoord(x = $x, y = $y)"
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }
    }

}