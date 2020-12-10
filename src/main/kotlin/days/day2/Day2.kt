package days.day2

import InputReader

class Day2 {

    fun run() {
        val input = InputReader("day2_input.txt").fileContents
        val entries = conditionInput(input)
        var validCount = 0
        entries.forEach { if (it.validateNew()) { validCount++ } }
        println(validCount)

    }

    private fun conditionInput(input: List<String>): List<PasswordEntry>  = input.map { PasswordEntry(it) }

    class PasswordEntry(entry: String) {
        private val policy = entry.split(":")[0].trim()
        private val password = entry.split(":")[1].trim()
        private val characterConstraint = policy.split(" ")[1].trim().toCharArray()[0]
        private val range = policy.split(" ")[0].trim()
        private val firstConstraint = range.split("-")[0].trim().toInt()
        private val secondConstraint = range.split("-")[1].trim().toInt()

        fun validateOld(): Boolean {
            val quantity = password.count { char -> char == characterConstraint }
            return (quantity in firstConstraint .. secondConstraint)
        }

        fun validateNew(): Boolean {
            val firstValue = password[firstConstraint - 1]
            val secondValue = password[secondConstraint - 1]
            return ((firstValue == characterConstraint) xor (secondValue == characterConstraint))
        }
    }
}