import java.io.File

enum class D22BoardTile {
    Empty,
    Floor,
    Wall,;
}

fun fromCharacter(c: Char) = when (c) {
    ' ' -> D22BoardTile.Empty
    '.' -> D22BoardTile.Floor
    '#' -> D22BoardTile.Wall
    else -> throw IllegalArgumentException("invalid character")
}

data class Orientation(val xDiff: Int, val yDiff: Int) {
    fun turnRight() = Orientation(-yDiff, xDiff)
    fun turnLeft() = Orientation(yDiff, -xDiff)
    fun toPassword(): Int {
        if (xDiff == 1) {
            assert(yDiff == 0)
            return 0
        } else if (xDiff == -1) {
            assert(yDiff == 0)
            return 2
        } else if (yDiff == 1) {
            assert(xDiff == 0)
            return 1
        } else {
            assert(xDiff == 0 && yDiff == -1)
            return 3
        }
    }
}

class Character(var position: Position, var orientation: Orientation)

operator fun Position.plus(orientation: Orientation) = Position(x + orientation.xDiff, y + orientation.yDiff)

operator fun Position.minus(orientation: Orientation) = Position(x - orientation.xDiff, y - orientation.yDiff)

fun moveCharacter(board: List<List<D22BoardTile>>, character: Character, distance: Int) {
    repeat(distance) {
        val newPos = character.position + character.orientation
        when (board[newPos.y][newPos.x]) {
            D22BoardTile.Empty -> {
                var nextPos = character.position - character.orientation
                while (board[nextPos.y][nextPos.x] != D22BoardTile.Empty) {
                    character.position = nextPos
                    nextPos = character.position - character.orientation
                }
            }
            D22BoardTile.Wall -> {
                return
            }
            D22BoardTile.Floor -> {
                character.position = newPos
            }
        }
    }
}

fun findStartingPosition(board: List<List<D22BoardTile>>): Position {
    val row = board[1]
    val x = row.indexOf(D22BoardTile.Floor)
    val y = 1
    assert(x != -1)
    return Position(x, y)
}

fun main() {
    val lines = File("./build/resources/main/day22.in").readLines()
    val boardLines = lines.takeWhile { it != "" }
    val instructionLine = lines.last()

    val w = boardLines.maxOf { it.length }
    val h = boardLines.size

    val emptyLine = " ".repeat(w + 2)
    val paddedBoardLines: List<String> = (
            listOf(emptyLine)
            + boardLines.map { it.padEnd(w + 1, ' ').padStart(w + 2, ' ') }
            + emptyLine
            )
    val board = paddedBoardLines.map {
        it.map { c -> fromCharacter(c) }
    }

    val startingPos = findStartingPosition(board)
    val character = Character(startingPos, Orientation(1, 0))
    val regex = """R|L|\d+""".toRegex()
    regex.findAll(instructionLine)
        .forEach {
            val s = it.value
            val distance = s.toIntOrNull()
            if (distance == null) {
                when (s) {
                    "R" -> character.orientation = character.orientation.turnRight()
                    "L" -> character.orientation = character.orientation.turnLeft()
                    else -> throw Exception("Invalid instruction: $s")
                }
            } else {
                moveCharacter(board, character, distance)
            }
        }

    val p1X = character.position.x
    val p1Y = character.position.y
    val part1 = 1000 * p1Y + 4 * p1X + character.orientation.toPassword()
    println(part1)
}