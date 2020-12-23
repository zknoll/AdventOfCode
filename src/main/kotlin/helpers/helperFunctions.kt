package helpers

import java.util.*
import kotlin.collections.ArrayList

infix fun <T,H> H.pairWith(second: T): Pair<H,T> = Pair(this, second)

fun <T> Collection<T>.toArrayList() = ArrayList<T>(this)

fun <T> Collection<T>.toLinkedList() = LinkedList<T>(this)