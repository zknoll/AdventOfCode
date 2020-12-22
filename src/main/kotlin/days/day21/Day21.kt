package days.day21

import days.Day

class Day21: Day("day21_input_2.txt") {

    companion object {
        private val regex = "((?:(?:\\w+)\\s)+)\\(contains ((?:\\w+,?\\s?)+)\\)?".toRegex()
    }

    val conditionedInput = inputLines

    override fun part1() {
        val recipes = getRecipes(conditionedInput)
        recipes.forEach { println(it) }
        findNonViableAllergensCount(recipes).also { println(it.size) }
    }

    override fun part2() {
        findCanonicalDangerousIngredients(getRecipes(conditionedInput))
    }

    fun findCanonicalDangerousIngredients(recipes: List<Recipe>) {
        val viableAllergens = findViableAllergens(recipes)
        val definiteAllergens = HashMap<String, String>()
        viableAllergens.forEach { println(it) }
        while (viableAllergens.filter { it.value.size == 1 }.isNotEmpty()) {
            viableAllergens.filter { it.value.size == 1 }.forEach { definiteAllergen ->
                val a = definiteAllergen.value[0]
                definiteAllergens[definiteAllergen.key] = a
                viableAllergens.forEach { it.value.remove(a)  }
            }
        }
        definiteAllergens.forEach { println(it) }
        println(definiteAllergens.keys.sorted().map { definiteAllergens[it] }.joinToString(","))
        //println(definiteAllergens.map { it.value }.sorted().joinToString(","))
    }

    fun findNonViableAllergensCount(recipes: List<Recipe>): List<String> {
        val viableAllergens = findViableAllergens(recipes)
        val allIngredients = recipes.map { it.ingredients }.flatten()
        println("There are ${allIngredients.size} total ingredients")
        return allIngredients.filter { it !in viableAllergens.values.flatten() }
    }

    fun findViableAllergens(recipes: List<Recipe>): HashMap<String, ArrayList<String>> {
        val viableAllergens: HashMap<String, ArrayList<String>> = hashMapOf()
        val allIngredients: ArrayList<String> = arrayListOf()
        recipes.forEach { r ->
            allIngredients.addAll(r.ingredients)
            r.allergens.forEach { allergen ->
                if (!viableAllergens.containsKey(allergen)) {
                    viableAllergens[allergen] = ArrayList(r.ingredients)
                } else {
                    //viableAllergens[allergen]!!.forEach { if (it !in r.ingredients) viableAllergens[allergen]!!.remove(it)}
                    val iterator = viableAllergens[allergen]!!.iterator()
                    while(iterator.hasNext()) {
                        if (iterator.next() !in r.ingredients) {
                            iterator.remove()
                        }
                    }
                }
            }
        }
        return viableAllergens
    }

    fun getRecipes(input: List<String>): List<Recipe> {
        return input.map { line -> regex.matchEntire(line)!!.let {
            Recipe(it.groupValues[1].split(" ").filter { it.isNotBlank()}, it.groupValues[2].split(", ")) }
        }
    }

    data class Recipe(val ingredients: List<String>, val allergens: List<String>)
}