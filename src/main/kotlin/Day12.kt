import java.io.File
import java.util.LinkedList
import java.util.Queue

operator fun Position.plus(other: Position): Position {
    return Position(x + other.x, y + other.y)
}

val directions = listOf(Position(-1, 0), Position(1, 0), Position(0, -1), Position(0, 1))

fun getNeighbours(heightMap: List<IntArray>, pos: Position): Sequence<Position> {
    val currentHeight = heightMap[pos.x][pos.y]
    return directions
        .asSequence()
        .map { it + pos }
        .map { Pair(it, heightMap.elementAtOrNull(it.x)?.elementAtOrNull(it.y)) }
        .mapNotNull { if (it.second == null) null else Pair(it.first, it.second!!) }
        .filter { it.second <= currentHeight + 1 }
        .map { it.first }
}

fun getNeighbours2(heightMap: List<IntArray>, pos: Position): Sequence<Position> {
    val currentHeight = heightMap[pos.x][pos.y]
    return directions
        .asSequence()
        .map { it + pos }
        .map { Pair(it, heightMap.elementAtOrNull(it.x)?.elementAtOrNull(it.y)) }
        .mapNotNull { if (it.second == null) null else Pair(it.first, it.second!!) }
        .filter { it.second >= currentHeight - 1 }
        .map { it.first }
}

fun main() {
    val lines = File("./build/resources/main/day12.in").readLines()

    val heightMap: List<IntArray> = lines
        .map { line -> line.map {
            when (it) {
                'S' -> 0
                'E' -> 25
                else -> it - 'a'
            }
        }.toIntArray() }

    var sPos: Position? = null
    var ePos: Position? = null
    for (i in lines.indices) {
        for (j in lines[i].indices) {
            when (lines[i][j]) {
                'S' -> sPos = Position(i, j)
                'E' -> ePos = Position(i, j)
            }
        }
    }

    if (sPos == null || ePos == null)
        throw Exception()

    val distance: List<IntArray> = heightMap
        .map { row -> row.map { -1 }.toIntArray() }

    val q: Queue<Position> = LinkedList()
    q.add(sPos)
    distance[sPos.x][sPos.y] = 0

    while (!q.isEmpty()) {
        val pos: Position = q.remove()
        val dist = distance[pos.x][pos.y]

        if (pos == ePos) {
            break
        }

        for (neighbourPos in getNeighbours(heightMap, pos)) {
            if (distance[neighbourPos.x][neighbourPos.y] >= 0)
                continue
            q.add(neighbourPos)
            distance[neighbourPos.x][neighbourPos.y] = dist + 1
        }
    }

    println(distance[ePos.x][ePos.y])

    q.clear()
    for (l in distance) {
        l.fill(-1)
    }
    q.add(ePos)
    distance[ePos.x][ePos.y] = 0

    var lastPos: Position = ePos

    while (!q.isEmpty()) {
        lastPos = q.remove()
        val dist = distance[lastPos.x][lastPos.y]

        if (heightMap[lastPos.x][lastPos.y] == 0)
            break

        for (neighbourPos: Position in getNeighbours2(heightMap, lastPos)) {
            if (distance[neighbourPos.x][neighbourPos.y] >= 0)
                continue
            q.add(neighbourPos)
            distance[neighbourPos.x][neighbourPos.y] = dist + 1
        }
    }

    println(distance[lastPos.x][lastPos.y])
}
