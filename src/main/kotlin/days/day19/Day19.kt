package days.day19

import days.Day

class Day19: Day("day19_input.txt") {

    companion object {
        private val elemRulesRegex = "^\\d+: \"(a|b)\"$".toRegex()
        private val chainRulesRegex = "^\\d+: (\\d+\\s?)+(\\| )?((\\d+\\s?)+)?\$".toRegex()
        private val messagesRegex = "^(a|b)+\$".toRegex()
    }
    private val input = inputLines
    private val rules: ArrayList<Rule>
        get() = ArrayList<Rule>(findElementaryRules(input)).also {
            it.addAll(findChainRules(input))
        }

    private val messages = findMessages(input)
    override fun part1() {
        val part1Rules = rules
        establishRulesRelationships(part1Rules)
        val regexString = "^${part1Rules.find { it.ruleNum == 0 }!!.getRegexString()}$"
        println(messages.filter { it.matches(regexString.toRegex()) }.size)
    }

    override fun part2() {
        val part2Rules = rules
        part2Rules.remove(part2Rules.find { it.ruleNum == 8 }!!)
        part2Rules.remove(part2Rules.find { it.ruleNum == 11}!!)
        part2Rules.add(Rule(8, "42 | 42 8"))
        part2Rules.add(Rule(11, "42 31 | 42 11 31"))
        establishRulesRelationships(part2Rules)

        val regexString = "^${part2Rules.find { it.ruleNum == 0 }!!.getRegexString()}$"
        println(regexString)
        println(messages.filter { it.matches(regexString.toRegex()) }.size)
    }

    private fun establishRulesRelationships(rules: List<Rule>) {
        rules.forEach { rule ->
            val ids = rule.getDependentRuleIds()
            ids.forEach { id -> rule.linkedRules.add(rules.find { it.ruleNum == id }!!) }
        }
    }

    private fun findElementaryRules(input: List<String>): List<ElementaryRule> {
        return input.filter { it.matches(elemRulesRegex) }.map {
            ElementaryRule(
                ruleNum = it.split(": ")[0].toInt(),
                pattern = it.split(": ")[1].replace("\"", "")
            )
        }
    }

    private fun findChainRules(input: List<String>): List<Rule> {
        return input.filter { it.matches(chainRulesRegex) }.map {
            Rule(
                ruleNum = it.split(": ")[0].toInt(),
                pattern = it.split(": ")[1].replace("\"", "")
            )
        }
    }

    private fun findMessages(input: List<String>): List<String> = input.filter { it.matches(messagesRegex) }.map { it.trim() }


    open class Rule(val ruleNum: Int, val pattern: String) {
        val linkedRules: ArrayList<Rule> = arrayListOf()
        private var selfReferentialCounter = 0
        open fun getRegexString(): String {
            var reg = "(?:"
            val splitPattern = pattern.split("|")
            val primaryRuleIds = splitPattern.map { it.trim() }[0].split(" ").map { it.toInt() }
            primaryRuleIds.forEach { id -> reg += "(?:${linkedRules.find { it.ruleNum == id }!!.getRegexString()})" }
            if (pattern.split("|").size != 1) {
                reg += "|"
                val secondaryRuleIds = ArrayList(splitPattern.map { it.trim() }[1].split(" ").map { it.toInt() })
                if (secondaryRuleIds.contains(ruleNum)) {
                    selfReferentialCounter += 1
                    if (selfReferentialCounter > 5) {
                        secondaryRuleIds.remove(ruleNum) // this should halt our infinite nesting...
                    }
                }
                secondaryRuleIds.forEach { id -> reg += "(${linkedRules.find { it.ruleNum == id }!!.getRegexString()})"}
            }
            reg += ")"
            return reg
        }

        fun getDependentRuleIds(): Set<Int> {
            return try {
                pattern.split("|").asSequence().map { it.trim() }.map { it.split(" ") }.flatten().map { it.toInt() }.toSet()
            } catch(e: Exception) {
                return setOf()
            }
        }

    }
    class ElementaryRule(ruleNum: Int, pattern: String): Rule(ruleNum, pattern) {
        override fun getRegexString()  = pattern.replace("\"", "")
    }
}