package days.day1

class Day1 {

    fun findSumValueInArray(list: List<Int>, searchValue: Int): Pair<Int, Int>? {
        var result: Pair<Int, Int>? = null
        for (i in 0 until list.size - 1) {
            for (j in i+1 until list.size) {
                if (list[i] + list[j] == searchValue) {
                    result = Pair(i, j)
                    break;
                }
            }
        }
        return result
    }

    fun findTripleSumValueInArray(list: List<Int>, searchValue: Int): Triple<Int, Int, Int>? {
        var result: Triple<Int, Int, Int>? = null
        for (i in 0 until list.size - 2) {
            for (k in i+2 until list.size) {
                for (j in i+1 until k) {
                    if (list[i] + list[j] + list[k] == searchValue) {
                        result = Triple(i,j,k)
                    }
                }
            }
        }
        return result
    }
}