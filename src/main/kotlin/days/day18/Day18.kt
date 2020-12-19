package days.day18

import days.Day
import java.util.*
import kotlin.collections.ArrayList

class Day18: Day("day18_input.txt") {

    private val conditionedInput = inputLines.map { it.replace(" ", "") }.map { Expression(it) }

    override fun part1() = conditionedInput.map { exp -> exp.evaluate().also { println(it) } }.sum().also { println("Total sum = $it") }

    override fun part2() = conditionedInput.map { exp -> exp.evaluateWithAdditionPrecedence().also { println(it) } }.sum().also {println("Total sum part 2 = $it")}

    private data class Expression(val expressionString: String) {
        val operators: ArrayList<Char> = arrayListOf()
        val operands: ArrayList<Expression> = arrayListOf()
        var value: Long? = null

        init {
            if (expressionString.length != 1) {
                val iterator = expressionString.iterator()
                while (iterator.hasNext()) {
                    var char = iterator.next()
                    if (char == '(') {
                        //find closing parenthetical and create new expression
                        val stack: Stack<Char> = Stack()
                        var newExpressionString = ""
                        stack.push(char)
                        while (iterator.hasNext()) {
                            char = iterator.next()
                            if (char == '(') {
                                stack.push(char)
                                newExpressionString += char.toString()
                            } else if (char == ')') {
                                stack.pop()
                                if (stack.size == 0) {
                                    println("creating sub-expression $newExpressionString")
                                    operands.add(Expression(newExpressionString))
                                    break
                                } else {
                                    newExpressionString += char.toString()
                                }
                            } else {
                                newExpressionString += char.toString()
                            }
                        }
                    } else if (char in setOf('*', '+')) {
                        operators.add(char)
                    } else {
                        operands.add(Expression(char.toString())) // should create a simple expression with a value
                    }
                }
            } else
                value = expressionString.toLong()
        }

        fun evaluate(): Long {
            val ops = ArrayList(operators.map { it })
            val expressions = ArrayList(operands.map { Expression(it.expressionString) })
            return value ?: run {
                var total = expressions.removeFirst().evaluate()
                while (ops.isNotEmpty()) {
                    total = when (ops.removeFirst()) {
                        '+' -> { total + expressions.removeFirst().evaluate() }
                        '*' -> { total * expressions.removeFirst().evaluate() }
                        else -> throw Exception("Unable to perform calculation with unregocnized operand")
                    }
                }
                total
            }
        }

        fun evaluateWithAdditionPrecedence(): Long {
            val ops = ArrayList(operators.map { it })
            val expressions = ArrayList(operands.map { Expression(it.expressionString) })
            return value ?: run {
                var plusIndex = ops.indexOfFirst { it == '+' }
                while (plusIndex != -1) {
                    println(ops)
                    println(expressions.map { it.expressionString })
                    val value = expressions[plusIndex].evaluateWithAdditionPrecedence() + expressions[plusIndex + 1].evaluateWithAdditionPrecedence()
                    expressions.removeAt(plusIndex + 1)
                    expressions.removeAt(plusIndex)
                    expressions.add(plusIndex, Expression("1").apply { this.value = value })
                    ops.removeAt(plusIndex)
                    plusIndex = ops.indexOfFirst { it == '+' }
                }
                // we've evaluated all additions, now lets do multiplications
                var total = expressions.removeFirst().evaluateWithAdditionPrecedence()
                while (ops.isNotEmpty()) {
                    ops.removeFirst()
                    total *= expressions.removeFirst().evaluateWithAdditionPrecedence()
                }
                total
            }
        }
    }
}