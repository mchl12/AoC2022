package day22

import java.io.File
import kotlin.math.max
import kotlin.math.min

const val BLOCK_SIZE = 50

fun main() {
    val lines = File("./build/resources/main/day22.in").readLines()
    val width = lines.maxOf { it.length }

    val boardLines = lines.takeWhile { it != "" }
    val instructionLine = lines.last()

    val emptyLine = " ".repeat(width + 2)
    val paddedLines = listOf(emptyLine) + boardLines.map { it.padEnd(width + 1).padStart(width + 2) } + emptyLine

    val edges = makeEdges(paddedLines)

    val startX = paddedLines[1].indexOfFirst { it != ' ' }
    val startPos = Vec2(startX, 1)
    var character = Character(startPos, Orientation.Right)
    val instructionRegex = """\d+|R|L""".toRegex()

    instructionRegex.findAll(instructionLine).forEach {
        val s = it.value
        val parsedInt = s.toIntOrNull()
        if (parsedInt == null) {
            when (s) {
                "R" -> character.orientation = character.orientation.getRight()
                "L" -> character.orientation = character.orientation.getLeft()
                else -> throw Exception("failed regex")
            }
        } else {
            repeat(parsedInt) {
                val newCharacter = getNextCharacter(paddedLines, edges, character)
                if (paddedLines[newCharacter.position.y][newCharacter.position.x] == '#') {
                    return@forEach
                }
                character = newCharacter
            }
        }
    }

    println(character.getPassword())
}

fun makeEdges(board: List<String>): List<Edge> {
    val xStart = board[1].indexOfFirst { it != ' ' }
    val startPos = Vec2(xStart, 1)
    var pos = startPos
    var orientation = Orientation.Right
    val mutEdges = mutableListOf<Edge>()

    while (mutEdges.isEmpty() || pos != startPos) {
        val start = pos
        pos += orientation.diff * (BLOCK_SIZE - 1)
        val end = pos
        val xRange = min(start.x, end.x)..max(start.x, end.x)
        val yRange = min(start.y, end.y)..max(start.y, end.y)
        mutEdges.add(Edge(xRange, yRange, orientation.getLeft()))

        val leftTurnPos = pos + orientation.diff + orientation.getLeft().diff
        if (board[leftTurnPos.y][leftTurnPos.x] != ' ') {
            pos = leftTurnPos
            orientation = orientation.getLeft()
            continue
        }

        val straightPos = pos + orientation.diff
        if (board[straightPos.y][straightPos.x] != ' ') {
            pos = straightPos
            continue
        }

        orientation = orientation.getRight()
    }

    val numEdges = mutEdges.size
    mutEdges.add(mutEdges[0])
    val edges = mutEdges.toList()

    for (i in 0 until numEdges) {
        if (edges[i].orientation.getLeft() != edges[i + 1].orientation)
            continue

        var leftI: Int
        var rightI: Int
        var newLeftI = i
        var newRightI = i + 1

        do {
            leftI = newLeftI
            rightI = newRightI
            edges[leftI].connectedEdge = edges[rightI]
            edges[rightI].connectedEdge = edges[leftI]
            newLeftI = (leftI - 1).mod(numEdges)
            newRightI = (rightI + 1).mod(numEdges)
        } while (edges[newLeftI].orientation.getRight() != edges[leftI].orientation
            || edges[rightI].orientation.getRight() != edges[newRightI].orientation)
    }

    return edges.dropLast(1)
}

fun getNextCharacter(board: List<String>, edges: List<Edge>, character: Character): Character {
    val candidatePosition = character.position + character.orientation.diff
    if (board[candidatePosition.y][candidatePosition.x] != ' ')
        return character.copy(position = candidatePosition)

    val edge = edges.find { it.isOnEdge(character) }!!
    return edge.transformPosition(character.position)
}

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(that: Vec2) = Vec2(x + that.x, y + that.y)
    operator fun minus(that: Vec2) = Vec2(x - that.x, y - that.y)
    operator fun unaryMinus() = Vec2(-x, -y)
    operator fun times(m: Int) = Vec2(x * m, y * m)
}

data class Character(var position: Vec2, var orientation: Orientation) {
    fun getPassword() = position.y * 1000 + position.x * 4 + orientation.score
}

enum class Orientation (val diff: Vec2, val lowToHigh: Boolean, val score: Int) {
    Left(Vec2(-1, 0), false, 2),
    Right(Vec2(1, 0), true, 0),
    Top(Vec2(0, -1), true, 3),
    Bottom(Vec2(0, 1), false, 1);

    fun getOpposite() = when (this) {
        Left -> Right
        Right -> Left
        Top -> Bottom
        Bottom -> Top
    }

    fun getRight() = when (this) {
        Left -> Top
        Top -> Right
        Right -> Bottom
        Bottom -> Left
    }

    fun getLeft() = when (this) {
        Left -> Bottom
        Bottom -> Right
        Right -> Top
        Top -> Left
    }
}

class Edge(private val xRange: IntRange, private val yRange: IntRange, val orientation: Orientation) {

    lateinit var connectedEdge: Edge

    fun isOnEdge(character: Character) =
        character.position.x in xRange &&
                character.position.y in yRange &&
                character.orientation == orientation

    fun transformPosition(position: Vec2): Character {
        assert(position.x in xRange && position.y in yRange)

        val resultOrientation = connectedEdge.orientation.getOpposite()
        val offset = position.x - xRange.first + position.y - yRange.first
        val invert = orientation.lowToHigh == connectedEdge.orientation.lowToHigh
        val x: Int
        val y: Int
        if (connectedEdge.xRange.first == connectedEdge.xRange.last) {
            x = connectedEdge.xRange.first
            y = if (invert) connectedEdge.yRange.last - offset
            else connectedEdge.yRange.first + offset
        } else {
            assert (connectedEdge.yRange.first == connectedEdge.yRange.last)
            x = if (invert) connectedEdge.xRange.last - offset
            else connectedEdge.xRange.first + offset
            y = connectedEdge.yRange.first
        }

        return Character(Vec2(x, y), resultOrientation)
    }

    override fun toString(): String {
        return "${hashCode()}: $xRange, $yRange, $orientation, ${connectedEdge.hashCode()}"
    }
}