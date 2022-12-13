import java.io.File

abstract class D13Item : Comparable<D13Item> {
    abstract override fun compareTo(that: D13Item): Int
}

class D13List : D13Item() {
    val list: MutableList<D13Item> = mutableListOf()

    override fun compareTo(that: D13Item): Int {
        when (that) {
            is D13Int -> return compareTo(that.asList)
            is D13List -> return compareTo(that)
        }

        throw Exception("wtf!")
    }

    operator fun compareTo(that: D13List): Int {
        var i = 0
        while (i < list.size && i < that.list.size) {
            val comp = list[i].compareTo(that.list[i])
            if (comp != 0)
                return comp
            i++
        }
        val compLength = list.size.compareTo(that.list.size)
        if (compLength != 0)
            return compLength

        return 0
    }
}

class D13Int(private val value: Int) : D13Item() {
    val asList = toList()

    private fun toList(): D13List {
        val list = D13List()
        list.list.add(this)
        return list
    }

    override fun compareTo(that: D13Item): Int {
        when (that) {
            is D13Int -> return compareTo(that)
            is D13List -> return asList.compareTo(that)
        }

        throw Exception("wtf")
    }

    operator fun compareTo(that: D13Int): Int {
        return value.compareTo(that.value)
    }
}

var listBuildI = 0

fun myListFromInit(str: String): D13List {
    listBuildI = 0
    return myListFrom(str)
}

fun myListFrom(str: String): D13List {
    val list = D13List()
    listBuildI++

    while (true) {
        when (str[listBuildI]) {
            ',' -> listBuildI++
            ']' -> { listBuildI++; return list }
            '[' -> list.list.add(myListFrom(str))
            else -> {
                val relevantString = str.substring(listBuildI).takeWhile { it != ',' && it != ']' }
                list.list.add(D13Int(relevantString.toInt()))
                listBuildI += relevantString.length
            }
        }
    }
}

fun main() {
    val lines = File("./build/resources/main/day13.in").readText()

    val pairs = lines.split("\r\n\r\n")
        .map { val (l, r) = it.split("\r\n"); Pair(myListFromInit(l), myListFromInit(r)) }

    val part1 = pairs.mapIndexed { i, pair -> if (pair.first < pair.second) (i + 1) else 0 }
        .sum()

    println(part1)

    val divider1 = myListFromInit("[[2]]")
    val divider2 = myListFromInit("[[6]]")
    val allPackets = pairs.flatMap { it.toList() } + divider1 + divider2
    val allPacketsSorted = allPackets.sorted()

    val index1 = allPacketsSorted.indexOf(divider1) + 1
    val index2 = allPacketsSorted.indexOf(divider2) + 1

    println(index1 * index2)
}