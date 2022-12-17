import java.io.File
import kotlin.math.*

fun main() {
    val sol = Solution()
    println(sol.part1(2022))
}

data class Shape(val positions: List<Position>, var offset: Position) {
    fun getPositionIterator() = positions.map { it + offset }
}

class Solution {
    private var jetIteration = 0
    private val push: BooleanArray
    private val field: List<BooleanArray> = List(9) { BooleanArray(10000000) { false } }
    private var highestY = 0
    private val shapes = listOf(
        Shape(listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0),
            Position(3, 0),
        ), Position(0, 0)
        ),
        Shape(listOf(
            Position(0, 1),
            Position(1, 1),
            Position(2, 1),
            Position(1, 0),
            Position(1, 2),
        ), Position(0, 0)
        ),
        Shape(listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0),
            Position(2, 1),
            Position(2, 2)
        ), Position(0, 0)
        ),
        Shape(listOf(
            Position(0, 0),
            Position(0, 1),
            Position(0, 2),
            Position(0, 3),
        ), Position(0, 0)
        ),
        Shape(listOf(
            Position(0, 0),
            Position(0, 1),
            Position(1, 0),
            Position(1, 1),
        ), Position(0, 0)
        ),
    )

    constructor() {
        val line = File("./build/resources/main/day17.in").readLines()[0]
        push = line.map { it == '>' }.toBooleanArray()
        for (x in 0 until 9) {
            field[x][0] = true
        }
        for (y in 0 until field[0].size) {
            field[0][y] = true
            field[8][y] = true
        }
    }

    fun part1(rockCount: Int): Int {
        for (i in 0 until rockCount) {
            val shape = getNextShape(i)
            var canFall = true
            while (canFall) {
                val right = getJetGoesRight(jetIteration)
                jetIteration++
                val direction = if (right)
                    Position(1, 0)
                else
                    Position(-1, 0)
                tryPushShape(shape, direction)
                canFall = tryPushShape(shape, Position(0, -1))
            }
            solidifyShape(shape)
        }
        return highestY
    }

    private fun getJetGoesRight(i: Int) = push[i % push.size]

    private fun getNextShape(iteration: Int): Shape {
        val i = iteration % shapes.size
        return shapes[i].copy(offset = Position(3, highestY + 4))
    }

    private fun solidifyShape(shape: Shape) {
        for (pos in shape.getPositionIterator()) {
            field[pos.x][pos.y] = true
            highestY = max(highestY, pos.y)
        }
    }

    private fun tryPushShape(shape: Shape, pushDirection: Position): Boolean {
        for (pos in shape.getPositionIterator()) {
            val newPos = pos + pushDirection
            if (field[newPos.x][newPos.y]) {
                return false
            }
        }

        shape.offset += pushDirection
        return true
    }
}