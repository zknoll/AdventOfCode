package days.day16

import days.Day

class Day16: Day("day16_input.txt") {
    private val rulesRegex = "^(\\D+): (\\d+)-(\\d+) or (\\d+)-(\\d+)\$".toRegex()

    val rules = inputLines.filter { it.matches(rulesRegex) }.also { println(it) }
    val myTicket = inputString.split("\r\n\r\n")[1].split("\r\n").map { it.trim() }.let { it.subList(1, it.size) }.also { println(it) }
    val otherTickets = inputString.split("\r\n\r\n")[2].split("\r\n").map { it.trim() }.let { it.subList(1, it.size) }.also { println(it) }

    override fun part1() = findInvalidTickets(rules, otherTickets)

    override fun part2() = 0

    private fun findInvalidTickets(rulesStrings: List<String>, otherTickets: List<String>) {
        val ticketRules = rulesStrings.map { ruleStr ->
            rulesRegex.matchEntire(ruleStr)?.let {
                TicketRule(it.groupValues[1],
                    validRange1 = it.groupValues[2].toInt() .. it.groupValues[3].toInt(),
                    validRange2 = it.groupValues[4].toInt() .. it.groupValues[5].toInt()
                )
            }
        }
        println(ticketRules)
        otherTickets.forEach {}
    }

    data class TicketRule(val name: String, val validRange1: IntRange, val validRange2: IntRange) {
        fun validate(value: Int) = (value in validRange1 || value in validRange2)
    }
}