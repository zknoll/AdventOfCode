package days.day7

import days.Day
import helpers.pairWith

class Day7: Day("day7_input.txt") {
    companion object {
        private val childRegex = "\\d (\\w* ){2}\\w*".toRegex()
        private val parentRegex = "^(\\w* ){2}\\w*".toRegex()
    }

    private val conditionedInput = inputLines.map {
        it.replace(",", "")
            .replace("bags", "bag")
            .replace(".", "")
    }

    override fun part1() = println(getNumberOfParents(betterCreateBagsWithRegex(conditionedInput).find { it.identifier == "shiny gold bag"}!!))
    override fun part2() = println(getChildren(betterCreateBagsWithRegex(conditionedInput).find { it.identifier == "shiny gold bag" }!!)).also {
        betterCreateBagsWithRegex(conditionedInput).map {
            getChildren(it)
        }.maxOrNull().also { println(it) }
    }

    private fun betterCreateBagsWithRegex(input: List<String>): List<Bag> {
        // bags.size = length of the input file because each bag has 1 line with rules, so lets make them all up-front
        val bags = input.map { rule -> Bag(parentRegex.find(rule)?.value!!, arrayListOf(), arrayListOf()) }
        // next do the same thing for the child rules here and pair them with their quantities
        val childrenStringsAndQuantity = input.map { rule ->
            childRegex.findAll(rule).map { matchResult ->
                bags.find { it.identifier == matchResult.value.substring( 2 until matchResult.value.length) }!! pairWith matchResult.value.substring(0 until 1).toInt()
            }.toList()
        }
        // now for each bag, populate parent and children, we should be in all the same order between the two arrays
        bags.forEachIndexed { index, bag ->
            bag.children.addAll(childrenStringsAndQuantity[index])
            childrenStringsAndQuantity[index].forEach { it.first.viableParents.add(bag) }
        }
        return bags
    }

    private fun getNumberOfParents(bag: Bag): Set<Bag> {
        return bag.viableParents.toHashSet().also { it.addAll(bag.viableParents.map { getNumberOfParents(it) }.flatten()) }
    }
    private fun getChildren(bag: Bag): Long {
        return bag.children.sumBy { it.second } + bag.children.map { it.second * getChildren(it.first) }.sum()
    }

    private class Bag(val identifier: String, val viableParents: ArrayList<Bag>, val children: ArrayList<Pair<Bag, Int>>) {
        override fun toString(): String {
            return "Identifier = $identifier, viableParents = ${viableParents.map { it.identifier } }, children = ${children.map { "${it.second} ${it.first.identifier}"  }}"
        }
    }

    /*
    private fun createBagsWithRegex(input: List<String>): List<Bag> {
        val bags: ArrayList<Bag> = arrayListOf()
        input.forEach { rule ->
            val parentString = parentRegex.find(rule)?.value ?: throw Exception("Failed to parse parent")
            val childrenStringAndQuantity = childRegex.findAll(rule).map {
                it.value.substring(0 until 1).toInt() pairWith it.value.substring(2 until it.value.length)
            }.toList()
            val parent = bags.find { it.identifier == parentString }
                ?: Bag(parentString, arrayListOf(), arrayListOf()).also { bags.add(it) }
            childrenStringAndQuantity.forEach { child ->
                val childBag = bags.find { it.identifier == child.second }
                    ?: Bag(child.second, arrayListOf(), arrayListOf()).also { bags.add(it) }
                childBag.viableParents.add(parent)
                parent.children.add(childBag pairWith child.first)
            }
        }
        println(bags.size)
        return bags
    }

    private fun createBagsFromInput(input: List<String>): List<Bag> {
        // note to self, do this with a regex
        val bags: ArrayList<Bag> = arrayListOf()
        input.forEach { rule ->
            var values = rule.split("bag").map { it.trim() }
            values = values.subList(0, values.size - 1)
            //println(values)
            //val parent = Bag(values[0], arrayListOf()).also { bags.add(it) }
            //val parent = bags.find { it.identifier ==  values[0] } ?: run { Bag(values[0], arrayListOf()); bags.add(parent) }
            val parent = bags.find { it.identifier == values[0] } ?: run {
                val newBag = Bag(values[0], arrayListOf(), arrayListOf())
                bags.add(newBag)
                newBag
            }
            val listOfChildRules = values.subList(1, values.size)
            if (!(listOfChildRules.size == 1 && listOfChildRules[0] == "no other")) {
                val children = values.subList(1, values.size)
                    .map { it.split(" ")[0] to it.split(" ")[1].trim() + " " + it.split(" ")[2].trim() }
                children.forEach { child ->
                    bags.find { it.identifier == child.second }?.let {
                        it.viableParents.add(parent)
                        parent.children.add(it pairWith child.first.toInt())
                    } ?: run {
                        val newChild = Bag(child.second, arrayListOf(parent), arrayListOf())
                        parent.children.add(newChild pairWith child.first.toInt())
                        bags.add(newChild)
                    }
                }
            }
        }
        return bags
    } */
}