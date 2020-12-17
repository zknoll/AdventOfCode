package days.day17

import days.Day

class Day17: Day("day17_input.txt") {
    companion object {
        private const val ACTIVE = 1
        private const val INACTIVE = 0
    }

    override fun part1() = 0

    override fun part2() = 0

    private fun conditionInput(input: List<String>): HashSet<Cube> {
        //create initial cubes from input
        val cubeSet: HashSet<Cube> = hashSetOf()
        for (i in input.indices) {
            for (j in input[i].indices) {
                cubeSet.add(Cube(
                    location = Point3D(i, j, 0),
                    value = if (input[i][j] == '#') ACTIVE else INACTIVE)
                )
            }
        }
        // extend by 1 in all directions
        val xDims = Pair(cubeSet.maxByOrNull { it.location.x }!!.location.x, cubeSet.minByOrNull { it.location.x }!!.location.x)
        val yDims = Pair(cubeSet.maxByOrNull { it.location.y }!!.location.y, cubeSet.minByOrNull { it.location.y }!!.location.y)
        val zDims = Pair(cubeSet.maxByOrNull { it.location.z }!!.location.z, cubeSet.minByOrNull { it.location.z }!!.location.z)

        for (i in yDims.first .. yDims.second) {
            for (j in zDims.first .. zDims.second) {
                cubeSet.add(Cube(Point3D(xDims.second - 1, i, j), INACTIVE))
                cubeSet.add(Cube(Point3D(xDims.first + 1, i, j), INACTIVE))
            }
        }
        for (i in xDims.first .. xDims.second) {
            for (j in zDims.first .. zDims.second) {
                cubeSet.add(Cube(Point3D(yDims.second - 1, i, j), INACTIVE))
                cubeSet.add(Cube(Point3D(yDims.first + 1, i, j), INACTIVE))
            }
        }
        for (i in yDims.first .. yDims.second) {
            for (j in xDims.first .. xDims.second) {
                cubeSet.add(Cube(Point3D(xDims.second - 1, i, j), INACTIVE))
                cubeSet.add(Cube(Point3D(xDims.first + 1, i, j), INACTIVE))
            }
        }
        return cubeSet
    }

    data class Point3D(val x: Int, val y: Int, val z: Int)
    data class Cube(val location: Point3D, val value: Int)

    private fun Set<Cube>.get(x: Int, y: Int, z: Int) = this.find { it.location.x == x && it.location.y == y && it.location.z == z }!!
}