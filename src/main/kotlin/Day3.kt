import java.io.File
import kotlin.collections.intersect

fun getPriority(item: Char): Int {
    val code = item.code

    if (code >= 'a'.code && code <= 'z'.code)
        return code - 'a'.code + 1

    return code - 'A'.code + 27
}

fun main() {
    val lines = File("./build/resources/main/day3.in").readLines()

    val score1: Int = lines
        .map { Pair(it.substring(0, it.length / 2).toSet(), it.substring(it.length / 2, it.length).toSet()) }
        .map { it.first.intersect(it.second).toList()[0] }
        .sumOf { getPriority(it) }

    println(score1)

    val score2 = lines
        .asSequence()
        .map(String::toSet)
        .chunked(3)
        .map { it[0].intersect(it[1]).intersect(it[2]) }
        .map { it.toList()[0] }
        .sumOf { getPriority(it) }

    println(score2)
}