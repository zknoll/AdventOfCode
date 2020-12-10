class InputReader(filename: String) {

    val fileContents: List<String> = this::class.java.getResourceAsStream(filename).bufferedReader().readLines()
    val fileAsString: String = this::class.java.getResource(filename).readText(Charsets.UTF_8).trim()


    fun getInputFileContentsAsInts(): List<Int> {
        return try {
            fileContents.map { it.toInt() }
        } catch (e: Exception) {
            println("ERROR: Couldn't cast contents to integer")
            listOf()
        }
    }
}