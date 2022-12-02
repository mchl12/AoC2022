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

fun moveToMake(oMove: Char, result: Char): Char {
    if (oMove == 'A' && result == 'X') return 'Z'
    if (oMove == 'A' && result == 'Y') return 'X'
    if (oMove == 'A' && result == 'Z') return 'Y'
    if (oMove == 'B' && result == 'X') return 'X'
    if (oMove == 'B' && result == 'Y') return 'Y'
    if (oMove == 'B' && result == 'Z') return 'Z'
    if (oMove == 'C' && result == 'X') return 'Y'
    if (oMove == 'C' && result == 'Y') return 'Z'
    if (oMove == 'C' && result == 'Z') return 'X'

    throw IllegalArgumentException()
}

fun scoreOfMatch2(result: Char) = when (result) {
    'X' -> 0
    'Y' -> 3
    'Z' -> 6
    else -> throw IllegalArgumentException()
}

fun calculateScore(matches: List<Pair<Char, Char>>) =
    matches.sumOf { scoreOfMatch(it.first, it.second) + scoreOfMove(it.second) }

fun calculateScore2(matches: List<Pair<Char, Char>>) =
    matches.sumOf { scoreOfMatch2(it.second) + scoreOfMove(moveToMake(it.first, it.second)) }

fun main() {
    println(System.getProperty("user.dir"))

    val matches: List<Pair<Char, Char>> = File("./build/resources/main/day2.in")
        .readLines()
        .map { Pair(it[0], it[2]) }

    val score = calculateScore(matches)
    println(score)

    val score2 = calculateScore2(matches)
    println(score2)
}