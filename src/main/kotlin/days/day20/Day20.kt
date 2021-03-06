package days.day20

import days.Day

class Day20: Day("day20_input.txt") {

    companion object {
        private val tileRegex = "Tile (\\d+):\\r\\n((?:[.#]+\\r\\n)+)".toRegex()
    }

    val input = inputString

    val seaMonster: List<List<Int>> = listOf(
        "                  # ",
        "#    ##    ##    ###",
        " #  #  #  #  #  #   "
    ).map { line -> line.map { if (it == ' ') 0 else 1} }

    val confirmationNumber = seaMonster.map { it.sum() }.sum()


    override fun part1() {
        val tiles = conditionInput(input).also { it.forEach { println(it) } }
        val puzzle = Puzzle(tiles)
        val cornerTiles = puzzle.tileLinks.filter { it.value.size == 2 }.also {println(it.map { it.key.id }) }
        println(cornerTiles.map { it.key.id.toLong() }.reduce{ acc, l -> acc * l })
    }

    override fun part2() {
        val tiles = conditionInput(input)
        val puzzle = Puzzle(tiles)
        val images = arrayListOf(
            puzzle.image,
            puzzle.image.rotate90(),
            puzzle.image.rotate90().rotate90(),
            puzzle.image.rotate90().rotate90().rotate90(),
            puzzle.image.flip(),
            puzzle.image.flip().rotate90(),
            puzzle.image.flip().rotate90().rotate90(),
            puzzle.image.flip().rotate90().rotate90().rotate90()
        )
        val seamonsters = images.map { image ->
            convolveIndexTopLeft(image, seaMonster).flatten().count { it == confirmationNumber }.also { println(it) }
        }.sum()
        println(puzzle.image.flatten().sum())
        println("Total #'s = ${puzzle.image.flatten().sum()}, choppy seas number = ${puzzle.image.flatten().sum() - (15 * seamonsters)}")
        // convolveIndexTopLeft(puzzle.image, seaMonster).flatten().count { it == confirmationNumber }.also { println(it) }
        // convolveIndexTopLeft(puzzle.image.flip().rotate90(), seaMonster).flatten().count { it == confirmationNumber }.also { println(it) }

    }

    private fun conditionInput(input: String): List<Tile> {
        return tileRegex.findAll(input).map { result ->
            Tile(result.groupValues[1].toInt(), result.groupValues[2].trim().split("\r\n"))
        }.toList()
    }

    private class Puzzle(val tiles: List<Tile>) {
        val tileLinks: HashMap<Tile, ArrayList<Tile>> = hashMapOf()
        val cornerTiles: ArrayList<Tile> = arrayListOf()
        lateinit var image: List<List<Int>>
        init {
            tiles.forEach { tile -> tileLinks[tile] = arrayListOf() }
            tiles.forEach { tile ->
                tiles.forEach { other ->
                    if (tile.id != other.id) {
                        val matchingBorder = tile.borders.indexOfFirst { other.borders.contains(it) }
                        if (matchingBorder != -1) {
                            tileLinks[tile]!!.add(other)
                        }
                    }
                }
            }
            cornerTiles.addAll(findCornerTiles())
            buildFromCorner(cornerTiles.first())
        }

        private fun findCornerTiles(): List<Tile> {
            return tileLinks.filter { it.value.size == 2}.map { it.key }
        }

        private fun buildFromCorner(corner: Tile) {
            // orient so that our tile is top left
            val firstMatch = corner.borders.map { tileLinks[corner]!![0].borders.contains(it) }.indexOfFirst { it }
            val secondMatch = corner.borders.map { tileLinks[corner]!![1].borders.contains(it) }.indexOfFirst { it }
            val shouldFlip: Boolean
            val shouldRotate: Boolean
            when (firstMatch) {
                0 -> {
                    when (secondMatch) {
                        2 -> {
                            // flip and rotate
                            shouldFlip = true
                            shouldRotate = true
                        }
                        3 -> {
                            shouldFlip = false
                            shouldRotate = true
                        }
                        else -> throw Exception()
                    }
                }
                1 -> {
                    when (secondMatch) {
                        2 -> {
                            shouldFlip = false
                            shouldRotate = false
                        }
                        3 -> {
                            shouldFlip = true
                            shouldRotate = false
                        }
                        else -> throw Exception()
                    }
                }
                2 -> {
                    when (secondMatch) {
                        0 -> {
                            shouldFlip = true
                            shouldRotate = true
                        }
                        1 -> {
                            shouldFlip = false
                            shouldRotate = false
                        }
                        else -> throw Exception()
                    }
                }
                3 -> {
                    when (secondMatch) {
                        0 -> {
                            shouldFlip = false
                            shouldRotate = true
                        }
                        1 -> {
                            shouldFlip = true
                            shouldRotate = false
                        }
                        else -> throw Exception()
                    }
                }
                else -> throw Exception()
            }
            if (shouldFlip) corner.flip()
            if (shouldRotate) corner.rotate(180)

            val puzzleStart = ArrayList(corner.getMap().map { ArrayList(it) })
            var tileToMatchRight = corner
            var tileToMatchDown = corner
            var tileWasMatched = true
            var row = 0
            while (tileWasMatched) {
                while (tileWasMatched) {
                    tileWasMatched = false
                    println("Searching for matches for ${tileToMatchRight.id} within ${tileLinks[tileToMatchRight]!!.map { it.id }}")
                    tileLinks[tileToMatchRight]!!.forEach { other ->
                        val candidates = other.getAllPermutations()
                        for (candidate in candidates) {
                            if (candidate.map { it.first() } == puzzleStart.subList(10*row, 10*(row+1)).map { it.last() }) {
                                println("found match")
                                //candidate.forEachIndexed { i, c -> println("${puzzleStart.subList(10*row, 10*(row+1))[i]} $c")  }
                                //throw Exception()
                                //puzzleStart.forEach { println(it) }
                                // concatenate
                                println("Matched tile ${tileToMatchRight.id} to ${other.id}")
                                puzzleStart.mapIndexed { i, r ->
                                    if (i >= 10 * row) {
                                        r.addAll(candidate[i % 10])
                                    }
                                }
                                tileWasMatched = true
                                tileToMatchRight = other
                                break
                            }
                        }
                    }
                    if (!tileWasMatched) println("Tile was not matched")
                }
                // now match down once
                tileWasMatched = false
                row++
                tileLinks[tileToMatchDown]!!.forEach { other ->
                    val candidates = other.getAllPermutations()
                    for (candidate in candidates) {
                        if (candidate.first() == puzzleStart.last().subList(0, candidate.first().size)) {
                            // add below
                            println("started new row")
                            println("Matched tile ${tileToMatchDown.id} to ${other.id}")
                            puzzleStart.addAll(candidate as Collection<ArrayList<Int>>)
                            tileWasMatched = true
                            tileToMatchDown = other
                            tileToMatchRight = other
                            break
                        }
                    }
                }
            }
            puzzleStart.forEach { println(it) }
            val borderRowsRemoved = puzzleStart.filterIndexed { i, _ -> i%10 != 0 && i%10 != 9 }
            val borderRowsAndColsRemoved = borderRowsRemoved.map { r -> r.filterIndexed { i, _ -> i%10 != 0 && i%10 != 9 } }

            image = borderRowsAndColsRemoved
            image.forEach { println(it) }
            println("Total image size = ${image.size} x ${image[0].size} (also last row = ${image.last().size})")
        }
    }

    private data class Tile(val id: Int, val matrix: List<String>) {
        val borders: ArrayList<String> = arrayListOf()
        private var isFlipped: Boolean = false
        private var rotation: Int = 0
        init {
            println(id)
            println(matrix)
            borders.add(matrix.first())
            borders.add(matrix.last())
            borders.add(matrix.map { it.last() }.joinToString(""))
            borders.add(matrix.map { it.first() }.joinToString(""))
            borders.addAll(borders.map { it.reversed() })
        }

        fun getAllPermutations(): List<List<List<Int>>> {
            return arrayListOf<List<List<Int>>>().apply {
                this.add(getMap(false, 0))
                this.add(getMap(false, 90))
                this.add(getMap(false, 180))
                this.add(getMap(false, 270))
                this.add(getMap(true, 0))
                this.add(getMap(true, 90))
                this.add(getMap(true, 180))
                this.add(getMap(true, 270))
            }
        }

        fun getMap(flipped: Boolean = isFlipped, rotate: Int = rotation): List<List<Int>> {
            val returnValue = if (!flipped) {
                matrix.map { line -> line.map { char -> if (char == '.') 0 else 1 } }
            } else {
                matrix.map { line -> line.reversed().map { char -> if (char == '.') 0 else 1 } }
            }

            return when (rotate) {
                0 -> returnValue
                90 -> returnValue.rotate90()
                180 -> returnValue.rotate90().rotate90()
                270 -> returnValue.rotate90().rotate90().rotate90()
                else -> throw Exception()
            }
        }

        fun rotate(value: Int) {
            if (value !in setOf(0,90,180, 270)) {
                throw Exception()
            }
            rotation = value
        }

        fun flip() {
            isFlipped = !isFlipped
        }

        fun List<List<Int>>.rotate90() = mapIndexed { i, row -> row.mapIndexed { j, _ -> this[j][size - i - 1] } }
    }

    fun List<List<Int>>.rotate90() = mapIndexed { i, row -> row.mapIndexed { j, _ -> this[j][size - i - 1] } }
    fun List<List<Int>>.flip() = map { it.reversed() }

    fun convolveIndexTopLeft(image: List<List<Int>>, kernel: List<List<Int>>): List<List<Int>> {
        if (image.size < kernel.size || image[0].size < kernel[0].size) {
            throw Exception()
        }

        val output = ArrayList<ArrayList<Int>>()
        for (i in 0 until image.size - kernel.size) {
            output.add(arrayListOf())
            for (j in 0 until image[0].size - kernel[0].size) {
                val subImage = image.subList(i, i+kernel.size).map { it.subList(j, j+kernel[0].size) }
                var sum = 0
                for (k in kernel.indices) {
                    for (m in kernel[0].indices) {
                        sum += subImage[k][m]
                            .and(kernel[k][m])
                    }
                }
                output[i].add(sum)
            }
        }
        return output
    }
}