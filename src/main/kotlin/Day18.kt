import java.io.File
import java.util.*

fun getNeighbours(pos: Triple<Int, Int, Int>): List<Triple<Int, Int, Int>> {
    val (x, y, z) = pos

    return listOf(
        Triple(x - 1, y, z),
        Triple(x + 1, y, z),
        Triple(x, y - 1, z),
        Triple(x, y + 1, z),
        Triple(x, y, z - 1),
        Triple(x, y, z + 1),
    )
}

fun strictNeighbours(pos: Triple<Int, Int, Int>) = getNeighbours(pos)
    .filter { (x, y, z) -> x in 0..21 && y in 0..21 && z in 0..21 }

fun getNumTrueNeighbours(hasCube: Array<Array<BooleanArray>>, pos: Triple<Int, Int, Int>) =
    getNeighbours(pos)
        .count { (x, y, z) -> hasCube
            .elementAtOrNull(x)
            ?.elementAtOrNull(y)
            ?.elementAtOrNull(z)
            ?: false }

fun part1(lines: List<String>) {
    val hasCube = Array(20) { Array(20) { BooleanArray(20) { false } } }

    for (line in lines) {
        val (x, y, z) = line.split(',').map(String::toInt)
        hasCube[x][y][z] = true
    }

    var total = 0
    for (x in 0..19) {
        for (y in 0..19) {
            for (z in 0..19) {
                if (!hasCube[x][y][z])
                    continue
                total += 6 - getNumTrueNeighbours(hasCube, Triple(x, y, z))
            }
        }
    }

    println(total)
}

fun part2(lines: List<String>) {
    val hasCube = Array(22) { Array(22) { BooleanArray(22) { false } } }

    for (line in lines) {
        val (x, y, z) = line.split(',').map(String::toInt)
        hasCube[x+1][y+1][z+1] = true
    }

    val hasWater = Array(22) { Array(22) { BooleanArray(22) { false } } }
    val q: Queue<Triple<Int, Int, Int>> = LinkedList()
    hasWater[0][0][0] = true
    q.add(Triple(0, 0, 0))

    while (!q.isEmpty()) {
        val pos = q.remove()
        for (npos in strictNeighbours(pos)) {
            val (x, y, z) = npos
            if (hasCube[x][y][z] || hasWater[x][y][z])
                continue
            hasWater[x][y][z] = true
            q.add(npos)
        }
    }

    var total = 0
    for (x in 0..21) {
        for (y in 0..21) {
            for (z in 0..21) {
                if (!hasCube[x][y][z])
                    continue
                total += getNumTrueNeighbours(hasWater, Triple(x, y, z))
            }
        }
    }

    println(total)
}

fun main() {
    val lines = File("./build/resources/main/day18.in").readLines()

    part1(lines)

    part2(lines)
}