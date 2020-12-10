package helpers

infix fun <T,H> H.pairWith(second: T): Pair<H,T> = Pair(this, second)