package days.day16

import days.Day
import java.math.BigInteger

class Day16: Day("day16_input.txt") {
    private val rulesRegex = "^(\\D+): (\\d+)-(\\d+) or (\\d+)-(\\d+)\$".toRegex()

    private val rules = inputLines.filter { it.matches(rulesRegex) }.also { println(it) }
    private val myTicket = inputString.split("\r\n\r\n")[1].split("\r\n").map { it.trim() }.let { it.subList(1, it.size) }.also { println(it) }
    private val otherTickets = inputString.split("\r\n\r\n")[2].split("\r\n").map { it.trim() }.let { it.subList(1, it.size) }.also { println(it) }

    override fun part1() = findTicketErrorRate(getRules(rules), otherTickets)

    override fun part2() {
        val validTickets = filterInvalidTickets(getRules(rules), otherTickets).also { println("Found valid tickets of length ${it.size}") }
        if (validTickets.isNotEmpty()) {
            val ruleOrder = findRuleOrder(getRules(rules), validTickets)
            var product: BigInteger = BigInteger.valueOf(1)
            val ticket = myTicket[0].split(",").map { it.trim().toInt() }

            ruleOrder.filter { it.key.name.contains("departure") }.forEach { departureRules ->
                product *= BigInteger.valueOf(ticket[departureRules.value].toLong())
            }
            println(product)
        }
    }

    private fun findTicketErrorRate(ticketRules: List<TicketRule>, otherTickets: List<String>): Int {
        return otherTickets.map { ticket ->
            val values = ticket.split(",").map { it.toInt() }
            values.map { value -> if (ticketRules.map { rule -> rule.validate(value) }.reduce { acc, b -> acc || b }) 0 else value }.sum()
        }.sum()
    }

    private fun filterInvalidTickets(ticketRules: List<TicketRule>, otherTickets: List<String>): List<String> {
        return otherTickets.filter { ticket ->
            val values = ticket.split(",").map { it.toInt() }
            values.map { value ->
                if (ticketRules.map { rule -> rule.validate(value) }.also { println(it) }.reduce { acc, b -> acc || b }) 0 else value
            }.sum() == 0
        }
    }

    private fun getRules(rulesStrings: List<String>) = rulesStrings.map { ruleStr ->
        rulesRegex.matchEntire(ruleStr)!!.let {
            TicketRule(it.groupValues[1],
                validRange1 = it.groupValues[2].toInt() .. it.groupValues[3].toInt(),
                validRange2 = it.groupValues[4].toInt() .. it.groupValues[5].toInt()
            )
        }
    }

    private fun findRuleOrder(rules: List<TicketRule>, ticketsStrings: List<String>): HashMap<TicketRule, Int> {
        val tickets = ticketsStrings.map {ticketStr -> ticketStr.split(",").map { it.trim().toInt() }}
        val rulePossibilities: HashMap<Int, HashSet<TicketRule>> = hashMapOf()
        val ruleOrder: HashMap<TicketRule, Int> = hashMapOf()
        val fixedRules: ArrayList<TicketRule> = arrayListOf()
        for (rule in rules) {
            for (i in tickets[0].indices) {
                val field = tickets.map { it[i] }
                if (field.map { rule.validate(it) }.reduce { acc, b -> acc && b }) {
                    if (rulePossibilities[i] == null) {
                        rulePossibilities[i] = hashSetOf(rule)
                    } else {
                        rulePossibilities[i]!!.add(rule)
                    }
                }
            }
        }
        // now we have all the sets individually, lets start eliminating numbers that only have one
        while (rulePossibilities.map { it.value.size }.sum() > 0 ) {
            val iterator = rulePossibilities.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.value.size == 1) {
                    println("Found unique rule constraint ${entry.key} = ${entry.value.first()}")
                    val rule = entry.value.first()
                    ruleOrder[rule] = entry.key
                    fixedRules.add(rule)
                    rulePossibilities.forEach { it.value.remove(rule) }
                    rulePossibilities.forEach { println(it) }
                    break
                }
            }

            // now we need to set all rules that only fit one position
            // check if that rule only exists in one valid position
            rules.forEach { rule ->
                if (rule !in fixedRules) {
                    println("Checking rule ${rule.name}")
                    val validPositions = rulePossibilities.filter { it.value.contains(rule) }
                    if (validPositions.size == 1) {

                        println("Found a rule with only one valid position ${validPositions.entries.first().key}, = $rule")
                        ruleOrder[rule] = validPositions.entries.first().key
                        fixedRules.add(rule)
                        rulePossibilities.forEach { it.value.remove(rule) }
                        rulePossibilities[validPositions.entries.first().key]!!.clear()
                        rulePossibilities.forEach { println(it) }
                    }
                }
            }

            // and then loop this until we eliminate everything
        }

        ruleOrder.forEach { println(it) }
        return ruleOrder
    }

    data class TicketRule(val name: String, val validRange1: IntRange, val validRange2: IntRange) {
        fun validate(value: Int) = (value in validRange1 || value in validRange2)
    }
}