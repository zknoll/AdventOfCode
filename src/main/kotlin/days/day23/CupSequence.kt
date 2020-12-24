package days.day23


import java.util.*

class CupSequenceTemp(startingList: List<Int>, listSize: Int) {
    private val backingMap: HashMap<Int, CupSequenceEntry> = hashMapOf()
    var first: CupSequenceEntry? = null
        private set

    init {
        backingMap[startingList[0]] = CupSequenceEntry(startingList[0])
        first = backingMap[startingList[0]]
        for (i in 1 until startingList.size) {
            val newEntry = CupSequenceEntry(startingList[i])
            val prevEntry = backingMap[startingList[i-1]]
            backingMap[startingList[i]] = newEntry
            prevEntry!!.next = newEntry
        }
        if (listSize == startingList.maxOrNull()!!) {
            backingMap[startingList.last()]!!.next = first
        } else {
            val bridgeEntry = CupSequenceEntry(startingList.size + 1)
            backingMap[startingList.size + 1] = bridgeEntry
            backingMap[startingList.last()]!!.next = bridgeEntry
            for (i in (startingList.size + 2)..listSize) {
                val newEntry = CupSequenceEntry(i)
                val prevEntry = backingMap[i - 1]
                backingMap[i] = newEntry
                prevEntry!!.next = newEntry
            }
            backingMap[listSize]!!.next = first
        }
    }

    fun removeThreeAfterAndInsert(removeAfter: CupSequenceEntry, insertAfter: CupSequenceEntry) {
        val toRemove: ArrayList<CupSequenceEntry> = arrayListOf()
        toRemove.add(removeAfter.next!!)
        toRemove.add(removeAfter.next!!.next!!)
        toRemove.add(removeAfter.next!!.next!!.next!!)

        removeAfter.next = toRemove.last().next

        // then join the removed to insertAfter and set the end to what insertAfter pointed to
        toRemove.last().next = insertAfter.next
        insertAfter.next = toRemove.first()
    }

    fun getEntryByValue(value: Int): CupSequenceEntry = backingMap[value]!!

    fun getNextThree(value: CupSequenceEntry): List<CupSequenceEntry> {
        val nextThree: ArrayList<CupSequenceEntry> = arrayListOf()
        nextThree.add(value.next!!)
        nextThree.add(nextThree.last().next!!)
        nextThree.add(nextThree.last().next!!)
        return nextThree
    }

    data class CupSequenceEntry constructor(val value: Int) {
        var next: CupSequenceEntry? = null
        override fun toString(): String {
            return "CupSequenceEntry(value = $value, nextValue = ${next?.value})"
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("(${first!!.value}, ")
        var next = first!!.next
        while (next != first) {
            sb.append("${next!!.value}, ")
            next = next.next!!
        }
        sb.append(")")
        return sb.toString()
    }
}