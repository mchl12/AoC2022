package day24

import java.io.File
import java.util.*

fun main() {
    val lines = File("./build/resources/main/day24.in").readLines()

    val startPosition = Vec2(1, 1)
    val finishLineY = lines.size

    val width = lines.maxOf(String::length)
    val height = lines.size + 2

    val board: Array<BooleanArray> = Array(width) { BooleanArray(height) { false } }
    val blizzards = mutableListOf<Blizzard>()

    for (x in 0 until width) {
        board[x][0] = true
        board[x][height - 1] = true
    }

    for ((y, line) in lines.withIndex()) {
        for ((x, c) in line.withIndex()) {
            when (c) {
                '#' -> board[x][y + 1] = true
                '<' -> blizzards.add(Blizzard(Vec2(x, y + 1), Orientation.Left))
                '>' -> blizzards.add(Blizzard(Vec2(x, y + 1), Orientation.Right))
                'v' -> blizzards.add(Blizzard(Vec2(x, y + 1), Orientation.Bottom))
                '^' -> blizzards.add(Blizzard(Vec2(x, y + 1), Orientation.Top))
                '.' -> {}
                else -> throw Exception("invalid character encountered")
            }
        }
    }

    val blizzardIterations = lcm(width - 2, height - 4)
    val blizzardPositions: Array<Array<BooleanArray>> = Array(blizzardIterations) {
        i ->
        val hasBlizzard = Array(width) { BooleanArray(height) { false } }
        for (blizzard in blizzards) {
            val pos = blizzard.getPositionAfterTime(i, width - 2, height - 4)
            hasBlizzard[pos.x][pos.y] = true
        }
        hasBlizzard
    }

    val visited: Array<Array<BooleanArray>> = Array(blizzardIterations) { Array(width) { BooleanArray(height) { false } } }
    visited[0][startPosition.x][startPosition.y] = true
    val queue: Queue<Pair<Vec2, Int>> = LinkedList()
    queue.add(Pair(startPosition, 0))

    var result: Int? = null
    while (!queue.isEmpty()) {
        val (pos, time) = queue.remove()
        if (pos.y >= finishLineY) {
            result = time
            break
        }

        val blizzardIt = (time + 1) % blizzardIterations
        val blizzardBoard = blizzardPositions[blizzardIt]
        for (orientation in Orientation.values()) {
            val newPos = pos + orientation.diff
            if (visited[blizzardIt][newPos.x][newPos.y])
                continue
            if (blizzardBoard[newPos.x][newPos.y] || board[newPos.x][newPos.y])
                continue

            visited[blizzardIt][newPos.x][newPos.y] = true
            queue.add(Pair(newPos, time + 1))
        }

        if (!visited[blizzardIt][pos.x][pos.y] && !blizzardBoard[pos.x][pos.y]) {
            visited[blizzardIt][pos.x][pos.y] = true
            queue.add(Pair(pos, time + 1))
        }
    }

    if (result == null) {
        println("Did not find a path")
    } else {
        println(result)
    }
}

fun gcd(a: Int, b: Int): Int {
    if (b == 0)
        return a
    return gcd(b, a%b)
}

fun lcm(a: Int, b: Int) = a * b / gcd(a, b)

class Blizzard(private val position: Vec2, private val orientation: Orientation) {
    fun getPositionAfterTime(time: Int, widthMod: Int, heightMod: Int): Vec2 {
        val realPosition = position - orientation.offset
        val nextPosition = realPosition + orientation.diff * time
        val moddedPosition = Vec2(nextPosition.x.mod(widthMod), nextPosition.y.mod(heightMod))
        return moddedPosition + orientation.offset
    }
}

enum class Orientation (val diff: Vec2, val offset: Vec2 = Vec2(1, 2)) {
    Left(Vec2(-1, 0)),
    Right(Vec2(1, 0)),
    Top(Vec2(0, -1)),
    Bottom(Vec2(0, 1));
}

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(that: Vec2) = Vec2(x + that.x, y + that.y)
    operator fun minus(that: Vec2) = Vec2(x - that.x, y - that.y)
    operator fun unaryMinus() = Vec2(-x, -y)
    operator fun times(m: Int) = Vec2(x * m, y * m)
}
