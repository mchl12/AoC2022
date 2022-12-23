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

fun moveCharacter2(board: List<List<D22BoardTile>>, character: Character, distance: Int) {
    repeat(distance) {
        var newPos = character.position + character.orientation
        var newOrientation = character.orientation
        if (board[newPos.y][newPos.x] == D22BoardTile.Empty) {
            val p = wrapPosition(newPos, newOrientation)
            assert(board[p.first.y][p.first.x] != D22BoardTile.Empty) { "invalid end point of wrap, starting at ${character.position} ${character.orientation}" }
            newPos = p.first
            newOrientation = p.second
        }
        when (board[newPos.y][newPos.x]) {
            D22BoardTile.Empty -> {
                throw Exception("wrap put us at another empty tile")
            }
            D22BoardTile.Wall -> {
                return
            }
            D22BoardTile.Floor -> {
                character.position = newPos
                character.orientation = newOrientation
            }
        }
    }
}

fun wrapPosition(position: Position, orientation: Orientation): Pair<Position, Orientation> {
    // left edge of 2
    if (position.x == 0) {
        val offset = position.y - (blockSize + 1)
        assert(offset in 0 until blockSize)
        // to bottom of 6, low to high
        return Pair(Position(blockSize * 4 - offset, blockSize * 3), Orientation(0, -1))
    }
    // bottom edge of 6
    if (position.x in blockSize * 3 + 1..blockSize * 4 && position.y == blockSize * 3 + 1) {
        val offset = position.x - (blockSize * 3) - 1
        // to left of 2, low to high
        return Pair(Position(1, blockSize * 2 - offset), Orientation(1, 0))
    }

    // left edge of 1
    if (position.x == blockSize * 2 && position.y in 1..blockSize) {
        val offset = position.y - 1
        // to top of 3, high to high
        return Pair(Position(blockSize + 1 + offset, blockSize + 1), Orientation(0, 1))
    }
    // top edge of 3
    if (position.x in blockSize + 1..blockSize * 2 && position.y == blockSize) {
        val offset = position.x - blockSize - 1
        // to left of 1, high to high
        return Pair(Position(blockSize * 2 + 1, 1 + offset), Orientation(1, 0))
    }

    // left edge of 5
    if (position.x == blockSize * 2 && position.y in 2 * blockSize + 1..3 * blockSize) {
        val offset = position.y - 2 * blockSize - 1
        // low to high, to bottom of 3
        return Pair(Position(2 * blockSize - offset, 2 * blockSize), Orientation(0, -1))
    }
    // bottom edge of 3
    if (position.x in blockSize + 1..blockSize * 2 && position.y == blockSize * 2 + 1) {
        val offset = position.x - blockSize - 1
        // to left of 5, low to high
        return Pair(Position(blockSize * 2 + 1, blockSize * 3 - offset), Orientation(1, 0))
    }

    // top edge of 2
    if (position.x in 1..blockSize && position.y == blockSize) {
        val offset = position.x - 1
        // from top of 1, low to high
        return Pair(Position(3 * blockSize - offset, 1), Orientation(0, 1))
    }
    // top edge of 1
    if (position.x in blockSize * 2 + 1..blockSize * 3 && position.y == 0) {
        val offset = position.x - blockSize * 2 - 1
        // to top of 2, low to high
        return Pair(Position(blockSize - offset, blockSize + 1), Orientation(0, 1))
    }

    // top edge of 6
    if (position.x in 3 * blockSize + 1..blockSize * 4 && position.y == 2 * blockSize) {
        val offset = position.x - 3 * blockSize - 1
        // to right of 4, low to high
        return Pair(Position(3 * blockSize, 2 * blockSize - offset), Orientation(-1, 0))
    }
    // right edge of 4
    if (position.x == 3 * blockSize + 1 && position.y in blockSize + 1..blockSize * 2) {
        val offset = position.y - blockSize - 1
        // to top of 6, low to high
        return Pair(Position(4 * blockSize - offset, blockSize * 2 + 1), Orientation(0, 1))
    }

    // right edge of 1
    if (position.x == 3 * blockSize + 1 && position.y in 1..blockSize) {
        val offset = position.y - 1
        // to right of 6, low to high
        return Pair(Position(4 * blockSize, 3 * blockSize - offset), Orientation(-1, 0))
    }
    // right edge of 6
    if (position.x == 4 * blockSize + 1 && position.y in 2 * blockSize + 1..3 * blockSize) {
        val offset = position.y - 1
        // to right of 1, low to high
        return Pair(Position(3 * blockSize, blockSize - offset), Orientation(-1, 0))
    }

    // bottom edge of 2
    if (position.x in 1..blockSize && position.y == blockSize * 2 + 1) {
        val offset = position.x - 1
        // to bottom of 5, low to high
        return Pair(Position(3 * blockSize - offset, 3 * blockSize), Orientation(0, -1))
    }
    // bottom edge of 5
    if (position.x in blockSize * 2 + 1..blockSize * 3 && position.y == blockSize * 3 + 1) {
        val offset = position.x - blockSize * 2 - 1
        // to bottom of 2, low to high
        return Pair(Position(blockSize - offset, blockSize * 2), Orientation(0, -1))
    }

    throw Exception("unrecognised connection: ${position.x} ${position.y}")
}

fun findStartingPosition(board: List<List<D22BoardTile>>): Position {
    val row = board[1]
    val x = row.indexOf(D22BoardTile.Floor)
    val y = 1
    assert(x != -1)
    return Position(x, y)
}

var blockSize = 0

fun main() {
    val lines = File("./build/resources/main/day22.in").readLines()
    val boardLines = lines.takeWhile { it != "" }
    val instructionLine = lines.last()

    val w = boardLines.maxOf { it.length }
    blockSize = w / 4

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

    character.position = findStartingPosition(board)
    character.orientation = Orientation(1, 0)
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
                moveCharacter2(board, character, distance)
            }
        }

    val p2X = character.position.x
    val p2Y = character.position.y
    val part2 = 1000 * p2Y + 4 * p2X + character.orientation.toPassword()
    println(part2)
}