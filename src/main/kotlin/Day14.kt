import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val lines = File("./build/resources/main/day14.in").readLines()

    val rockLines: List<List<Pair<Pair<Int, Int>, Pair<Int, Int>>>> = lines
        .map {line ->
            line.split(" -> ")
                .map {
                    val (x, y) = it.split(',')
                    Pair(x.toInt(), y.toInt())
                }
                .zipWithNext()
        }

    val scan: Array<BooleanArray> = Array(1000) { BooleanArray(200) { false } }
    val sand: Array<BooleanArray> = Array(1000) { BooleanArray(200) { false } }

    for (line in rockLines) {
        for ((coordinates1, coordinates2) in line) {
            val (x1, y1) = coordinates1
            val (x2, y2) = coordinates2

            if (x1 == x2) {
                for (i in min(y1, y2)..max(y1, y2)) {
                    scan[x1][i] = true
                }
            } else {
                assert(y1 == y2)
                for (i in min(x1, x2)..max(x1, x2)) {
                    scan[i][y1] = true
                }
            }
        }
    }

    var counter = 0
    while (dropSand(scan, sand)) {
        counter++
    }

    println(counter)

    val pairs = lines.flatMap { it.split(" -> ") }
        .map {
            val (x, y) = it.split(',')
            Pair(x.toInt(), y.toInt())
        }
    val maxY = pairs.maxOf { it.second }
    for (x in 0..999) {
        scan[x][maxY + 2] = true
    }

    while (dropSand2(scan, sand)) {
        counter++
    }

//    for (y in 0..11) {
//        for (x in 480..515) {
//            if (scan[x][y])
//                print('#')
//            else if (sand[x][y])
//                print('o')
//            else
//                print('.')
//        }
//        println()
//    }

    println(counter)
}

fun dropSand(scan: Array<BooleanArray>, sand: Array<BooleanArray>): Boolean {
    var sandX = 500
    var sandY = 0

    while (sandY < 199) {
        sandY++
        if (!scan[sandX][sandY] && !sand[sandX][sandY]) {
            continue
        }

        if (!scan[sandX - 1][sandY] && !sand[sandX - 1][sandY]) {
            sandX--
            continue
        }

        if (!scan[sandX + 1][sandY] && !sand[sandX + 1][sandY]) {
            sandX++
            continue
        }

        sand[sandX][sandY - 1] = true
        return true
    }

    return false
}

fun dropSand2(scan: Array<BooleanArray>, sand: Array<BooleanArray>): Boolean {
    var sandX = 500
    var sandY = 0

    if (sand[sandX][sandY])
        return false

    while (sandY < 199) {
        sandY++
        if (!scan[sandX][sandY] && !sand[sandX][sandY]) {
            continue
        }

        if (!scan[sandX - 1][sandY] && !sand[sandX - 1][sandY]) {
            sandX--
            continue
        }

        if (!scan[sandX + 1][sandY] && !sand[sandX + 1][sandY]) {
            sandX++
            continue
        }

        sand[sandX][sandY - 1] = true
        return true
    }

    throw Exception("sand bypassed wall at the bottom !")
}
