import java.io.File

data class Assignment(val low: Int, val high: Int) {
    operator fun contains(other: Assignment) = low <= other.low && high >= other.high
}

fun hasContainment(ass1: Assignment, ass2: Assignment) = ass1 in ass2 || ass2 in ass1

fun overlaps(ass1: Assignment, ass2: Assignment) = ass1.low <= ass2.high && ass2.low <= ass1.high

fun main() {
    val pattern = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()

    val lines = File("./build/resources/main/day4.in").readLines()

    val parsed: List<Pair<Assignment, Assignment>> = lines
        .map { pattern.matchEntire(it) }
        .map { it!!.groupValues }
        .map { Pair(Assignment(it[1].toInt(), it[2].toInt()), Assignment(it[3].toInt(), it[4].toInt())) }

    val part1: Int = parsed
        .map { hasContainment(it.first, it.second) }
        .map { if (it) 1 else 0 }
        .sum()

    println(part1)

    val part2: Int = parsed
        .map { overlaps(it.first, it.second) }
        .map { if (it) 1 else 0 }
        .sum()

    println(part2)
}