package days.day17

import days.Day

class Day17: Day("day17_input.txt") {
    companion object {
        private const val ACTIVE = 1
        private const val INACTIVE = 0
    }

    override fun part1() {
        val cubeSet = conditionInput(inputLines)
        for (i in 1 .. 6) {
            println("cycle $i")
            executeCycle(cubeSet)
            println()
            println()
            cubeSet.print()
            cubeSet.expand()
        }
        println(cubeSet.filter { it.value == ACTIVE }.size)
    }

    override fun part2() = 0

    private fun executeCycle(cubeSet: HashSet<Cube>): HashSet<Cube> {
        val (xDims, yDims, zDims, wDims) =  cubeSet.getDims()
        val cubesToSet = arrayListOf<Cube>()
        val cubesToClear = arrayListOf<Cube>()
        for (i in xDims.second .. xDims.first) {
            for (j in yDims.second .. yDims.first) {
                for (k in zDims.second .. zDims.first) {
                    for (l in wDims.second .. wDims.first) {
                        val cube = cubeSet.get(i, j, k, l)
                        val neighbors = countActiveNeighbors(cubeSet, cube.location)
                        if (cube.value == INACTIVE) {
                            if (neighbors == 3) {
                                // set cube
                                cubesToSet.add(cube)
                            }
                        } else {
                            if (neighbors !in setOf(2, 3)) {
                                cubesToClear.add(cube)
                            }
                        }
                    }
                }
            }
        }
        cubesToSet.forEach { cube -> println("Setting cube $cube"); cubeSet.get(cube.location.x, cube.location.y, cube.location.z, cube.location.w).value = ACTIVE }
        cubesToClear.forEach { cube -> println("Clearing cube $cube"); cubeSet.get(cube.location.x, cube.location.y, cube.location.z, cube.location.w).value = INACTIVE }
        return cubeSet
    }

    private fun countActiveNeighbors(cubeSet: HashSet<Cube>, location: Point4D): Int {
        var count = 0
        for (i in location.x-1 .. location.x+1) {
            for (j in location.y-1 .. location.y+1) {
                for (k in location.z-1 .. location.z+1) {
                    for (l in location.w - 1..location.w + 1) {
                        if (i != location.x || j != location.y || k != location.z || l != location.w) {
                            count += try {
                                cubeSet.get(i, j, k, l).value
                            } catch (e: Exception) {
                                0
                            }
                            if (count > 3) {
                                return count
                            }
                        }
                    }
                }
            }
        }
        return count
    }

    private fun conditionInput(input: List<String>): HashSet<Cube> {
        //create initial cubes from input
        val cubeSet: HashSet<Cube> = hashSetOf()
        for (i in input.indices) {
            for (j in input[i].indices) {
                cubeSet.add(Cube(
                    location = Point4D(i, j, 0, 0),
                    value = if (input[i][j] == '#') ACTIVE else INACTIVE)
                )
            }
        }
        cubeSet.expand()
        return cubeSet
    }

    data class Point4D(val x: Int, val y: Int, val z: Int, val w: Int)
    data class Cube(val location: Point4D, var value: Int)
    class Quad(val first: Pair<Int, Int>, val second: Pair<Int, Int>, val third: Pair<Int, Int>, val fourth: Pair<Int, Int>) {
        operator fun component1() = first
        operator fun component2() = second
        operator fun component3() = third
        operator fun component4() = fourth
    }

    private fun Set<Cube>.get(x: Int, y: Int, z: Int, w: Int) = this.find { it.location.x == x && it.location.y == y && it.location.z == z && it.location.w == w}!!
    private fun HashSet<Cube>.expand() {
        // extend by 1 in all directions
        val (xDims, yDims, zDims, wDims) = getDims()

        for (i in yDims.second .. yDims.first) {
            for (j in zDims.second .. zDims.first) {
                for (k in wDims.second .. wDims.first) {
                    if (this.filter { it.value == 1 }.minByOrNull { it.location.x }!!.location.x == xDims.second) {
                        this.add(Cube(Point4D(xDims.second - 1, i, j, k), INACTIVE))
                    }
                    if (this.filter { it.value == 1 }.maxByOrNull { it.location.x }!!.location.x == xDims.first) {
                        this.add(Cube(Point4D(xDims.first + 1, i, j, k), INACTIVE))
                    }
                }
            }
        }
        val (xDims2, yDims2, zDims2, wDims2) = getDims()
        for (i in xDims2.second .. xDims2.first) {
            for (j in zDims2.second .. zDims2.first) {
                for (k in wDims2.second .. wDims2.first) {
                    if (this.filter { it.value == 1 }.minByOrNull { it.location.y }!!.location.y == yDims.second) {
                        this.add(Cube(Point4D(i, yDims2.second - 1, j, k), INACTIVE))
                    }
                    if (this.filter { it.value == 1 }.maxByOrNull { it.location.y }!!.location.y == yDims.first) {
                        this.add(Cube(Point4D(i, yDims2.first + 1, j, k), INACTIVE))
                    }
                }
            }
        }
        val (xDims3, yDims3, zDims3, wDims3) = getDims()
        for (i in yDims3.second .. yDims3.first) {
            for (j in xDims3.second .. xDims3.first) {
                for (k in wDims3.second .. wDims3.first) {
                    if (this.filter { it.value == 1 }.minByOrNull { it.location.z }!!.location.z == zDims.second) {
                        this.add(Cube(Point4D(j, i, zDims3.second - 1, k), INACTIVE))
                    }
                    if (this.filter { it.value == 1 }.maxByOrNull { it.location.z }!!.location.z == zDims.first) {
                        this.add(Cube(Point4D(j, i, zDims3.first + 1, k), INACTIVE))
                    }
                }
            }
        }

        val (xDims4, yDims4, zDims4, wDims4) = getDims()
        for (i in xDims4.second .. xDims4.first) {
            for (j in yDims4.second .. yDims4.first) {
                for (k in zDims4.second .. zDims4.first) {
                    if (this.filter { it.value == 1 }.minByOrNull { it.location.w }!!.location.w == wDims.second) {
                        this.add(Cube(Point4D(i, j, k, wDims4.second - 1), INACTIVE))
                    }
                    if (this.filter { it.value == 1 }.maxByOrNull { it.location.w }!!.location.w == wDims.first) {
                        this.add(Cube(Point4D(i, j, k, wDims4.first + 1), INACTIVE))
                    }
                }
            }
        }
    }
    private fun HashSet<Cube>.getDims(): Quad {
        val xDims = Pair(this.maxByOrNull { it.location.x }!!.location.x, this.minByOrNull { it.location.x }!!.location.x)
        val yDims = Pair(this.maxByOrNull { it.location.y }!!.location.y, this.minByOrNull { it.location.y }!!.location.y)
        val zDims = Pair(this.maxByOrNull { it.location.z }!!.location.z, this.minByOrNull { it.location.z }!!.location.z)
        val wDims = Pair(this.maxByOrNull { it.location.w }!!.location.w, this.minByOrNull { it.location.w }!!.location.w)
        return Quad(xDims, yDims, zDims, wDims)
    }
    private fun HashSet<Cube>.print() {
        val (xDims, yDims, zDims) = this.getDims()
        for (k in zDims.second .. zDims.first) {
            println("z = $k")
            for (i in xDims.second..xDims.first) {
                for (j in yDims.second..yDims.first) {
                    print(this.get(i, j, k, 0).value)
                }
                println()
            }
        }
    }
}