import java.io.File

fun scoreOfMove(move: Char): Int = when (move) {
    'X' -> 1
    'Y' -> 2
    'Z' -> 3
    else -> throw IllegalArgumentException()
}

fun scoreOfMatch(oMove: Char, myMove: Char): Int {
    if (oMove == 'A' && myMove == 'X') return 3
    if (oMove == 'B' && myMove == 'Y') return 3
    if (oMove == 'C' && myMove == 'Z') return 3
    if (oMove == 'A' && myMove == 'Y') return 6
    if (oMove == 'B' && myMove == 'Z') return 6
    if (oMove == 'C' && myMove == 'X') return 6
    if (oMove == 'A' && myMove == 'Z') return 0
    if (oMove == 'B' && myMove == 'X') return 0
    if (oMove == 'C' && myMove == 'Y') return 0

    throw IllegalArgumentException()
}

fun calculateScore(matches: List<Pair<Char, Char>>) =
    matches.sumOf { scoreOfMatch(it.first, it.second) + scoreOfMove(it.second) }

fun main() {
    val matches: List<Pair<Char, Char>> = File("""D:\IdeaProjects\AoC2022\src\main\resources\day2.in""")
        .readLines()
        .map { Pair(it[0], it[2]) }

    val score = calculateScore(matches)
    println(score)
}