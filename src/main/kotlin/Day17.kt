import java.io.File
import kotlin.math.*

fun main() {
    val sol = Solution()
    println(sol.part1(2022))

    // part 2
    val sol2 = Solution()
    val (p1, p2) = sol2.part2(1000000)
    val (iIter1, iHeight1) = p1
    val (iIter2, iHeight2) = p2
    val iter1 = iIter1.toULong()
    val height1 = iHeight1.toULong()
    val iter2 = iIter2.toULong()
    val height2 = iHeight2.toULong()

    val period = iter2 - iter1
    val heightDiff = height2 - height1

    val periodsLeft = (1000000000000UL - iter1) / period
    val height = periodsLeft * heightDiff + height1
    println(height)
}

data class Shape(val positions: List<Position>, var offset: Position) {
    fun getPositionIterator() = positions.map { it + offset }
}

class Solution() {
    private var jetIteration = 0
    private val push: BooleanArray
    private val field: List<BooleanArray> = List(9) { BooleanArray(40000000) { false } }
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

    init {
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
                val right = push[jetIteration]
                incrementJet()
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

    fun part2(rockCount: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val hm = hashMapOf<Triple<UInt, UInt, ULong>, Pair<Int, Int>>()

        for (i in 0 until rockCount) {
            if (hm.containsKey(getHash(i))) {
                val p = hm[getHash(i)]!!
                val period = i - p.first
                if ((1000000000000UL - i.toULong()) % period.toULong() == 0UL)
                {
                    return Pair(p, Pair(i, highestY))
                }

            } else {
                hm[getHash(i)] = Pair(i, highestY)
            }
            val shape = getNextShape(i)
            var canFall = true
            while (canFall) {
                val right = push[jetIteration]
                incrementJet()
                val direction = if (right)
                    Position(1, 0)
                else
                    Position(-1, 0)
                tryPushShape(shape, direction)
                canFall = tryPushShape(shape, Position(0, -1))
            }
            solidifyShape(shape)
        }

        throw Exception("your input is impossible LOL")
    }

    private fun getHash(iter: Int): Triple<UInt, UInt, ULong> {
        val shapeIter = iter % shapes.size
        var caveHash = 0UL
        for (y in max(highestY - 8, 1)..max(highestY, 1)) {
            for (x in 1..7) {
                val i = y * 7 + (x - 1)

                if (field[x][y]) {
                    val mask = 1UL shl i
                    caveHash = caveHash or mask
                }
            }
        }

        return Triple(shapeIter.toUInt(), jetIteration.toUInt(), caveHash)
    }

    private fun incrementJet() {
        jetIteration++
        jetIteration %= push.size
    }

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