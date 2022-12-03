import java.io.File

fun getPriority(item: Char): Int {
    val code = item.code

    if (code >= 'a'.code && code <= 'z'.code)
        return code - 'a'.code + 1

    return code - 'A'.code + 27
}

fun main() {
    val score: Int = File("./build/resources/main/day3.in")
        .readLines()
        .map { Pair(it.substring(0, it.length / 2).toSet(), it.substring(it.length / 2, it.length).toSet()) }
        .map { it.first.intersect(it.second).toList()[0] }
        .sumOf { getPriority(it) }

    println(score)
}