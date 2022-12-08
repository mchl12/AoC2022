import java.io.File

data class Position(val x: Int, val y: Int)

fun setVisible(heights: List<IntArray>, visible: List<BooleanArray>, positions: Iterable<Position>): Unit {
    var maxHeight = -1
    for (pos in positions) {
        if (heights[pos.x][pos.y] > maxHeight) {
            maxHeight = heights[pos.x][pos.y]
            visible[pos.x][pos.y] = true
        }
    }
}

fun scoreOfLine(heights: List<IntArray>, maxHeight: Int, positions: Iterable<Position>): Int {
    var total = 0
    for (pos in positions) {
        if (heights[pos.x][pos.y] >= maxHeight)
            return total + 1
        total++
    }

    return total
}

fun scoreOf(heights: List<IntArray>, position: Position): Int {
    val maxHeight = heights[position.x][position.y]

    val w = heights.size
    val h = heights[0].size

    val leftIt: Iterable<Position> = (position.x - 1 downTo 0).map { Position(it, position.y) }
    val rightIt: Iterable<Position> = (position.x + 1 until w).map { Position(it, position.y) }
    val upIt: Iterable<Position> = (position.y - 1 downTo 0).map { Position(position.x, it) }
    val downIt: Iterable<Position> = (position.y + 1 until h).map { Position(position.x, it) }

    return (scoreOfLine(heights, maxHeight, leftIt)
        * scoreOfLine(heights, maxHeight, rightIt)
        * scoreOfLine(heights, maxHeight, upIt)
        * scoreOfLine(heights, maxHeight, downIt))
}

fun main() {
    val heights: List<IntArray> = File("./build/resources/main/day8.in")
        .readLines()
        .map { it.map(Char::digitToInt).toIntArray() }

    val w = heights.size
    val h = heights[0].size

    val visible: List<BooleanArray> = List<BooleanArray>(w) { _ -> BooleanArray(h) { _ -> false } }

    // left to right
    for (i in 0 until w) {
        val iterable: Iterable<Position> = (0 until h).map { Position(i, it) }
        setVisible(heights, visible, iterable)
    }

    // right to left
    for (i in 0 until w) {
        val iterable: Iterable<Position> = (h - 1 downTo 0).map { Position(i, it) }
        setVisible(heights, visible, iterable)
    }

    // top to down
    for (i in 0 until h) {
        val iterable: Iterable<Position> = (0 until w).map { Position(it, i) }
        setVisible(heights, visible, iterable)
    }

    // right to left
    for (i in 0 until h) {
        val iterable: Iterable<Position> = (w - 1 downTo 0).map { Position(it, i) }
        setVisible(heights, visible, iterable)
    }

    val count = visible
        .sumOf { it.count { a -> a } }

    println(count)

    val treesToConsider: Iterable<Position> = (1 until w - 1).flatMap {
            x -> (1 until h - 1).map { Position(x, it) }
    }

    val maxScore = treesToConsider.maxOf { scoreOf(heights, it) }

    println(maxScore)
}