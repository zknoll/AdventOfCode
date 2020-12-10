package days.day4

import days.Day
import helpers.pairWith

class Day4: Day("day4_input.txt") {

    companion object {
        private val colorRegex = "^#[0-9a-fA-F]{6}\$".toRegex()
        private val heightRegex = "(^(1[5-8][0-9]|19[0-3])cm\$)|(^(59|6[0-9]|7[0-6])in\$)".toRegex()
        private val passNumberRegex = "^[0-9]{9}\$".toRegex()
        private val eyeColorList = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
        private val passportFields = arrayListOf(
                EntryWithValidation("byr") { str -> str.toIntOrNull() in 1920..2002 },
                EntryWithValidation("iyr") { str -> str.toIntOrNull() in 2010..2020 },
                EntryWithValidation("eyr") { str -> str.toIntOrNull() in 2020..2030 },
                EntryWithValidation("hgt") { str -> str.matches(heightRegex) },
                EntryWithValidation("hcl") { str -> str.matches(colorRegex) },
                EntryWithValidation("ecl") { str -> str in eyeColorList },
                EntryWithValidation("pid") { str -> str.matches(passNumberRegex) }
        )
    }

    override fun part1() = println(validatePassports(conditionInput()).sumBy { if (it) 1 else 0 })
    override fun part2() { /* reimplemented in part 1 */ }

    private fun conditionInput(): List<String> {
        var acc = ""
        val conditionedInput: ArrayList<String> = arrayListOf()
        inputLines.forEach { line ->
            if (line.isNotBlank()) {
                acc += line.trim() + " "
            } else {
                conditionedInput.add(acc.trim())
                acc = ""
            }
        }
        if (acc.isNotBlank()) {
            conditionedInput.add(acc.trim())
        }
        return conditionedInput
    }

    private fun validatePassports(input: List<String>) = input.map { pass ->
        val fields = pass.split(" ").map { it.split(":")[0] pairWith it.split(":")[1] }
        val hasRequiredFields = passportFields.map { req -> req.str in fields.map { it.first } }.reduce { acc, b -> acc && b }
        if (hasRequiredFields) {
            passportFields.map { req -> req.validator(fields.find { it.first == req.str }?.second ?: "") }.reduce { acc, b -> acc && b }
        } else {
            false
        }
    }

    class EntryWithValidation(val str: String, val validator: (String) -> Boolean)
}
