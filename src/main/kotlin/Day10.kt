import java.io.File
import kotlin.math.abs

fun main() {
    val lines = File("./build/resources/main/day10.in").readLines()
    val adjLines = buildList<String> {
        add("noop")
        add("noop")
        for (line in lines) {
            if (line == "noop") {
                add("noop")
                continue
            }
            add(line)
            add("noop")
        }
    }

    val values = adjLines.runningFold(1, fun(acc: Int, str: String): Int {
        if (str == "noop")
            return acc

        val (_, addStr) = str.split(" ")
        val add = addStr.toInt()
        return acc + add
    })

    val sum = (values[20] * 20 + values[60] * 60 + values[100] * 100
        + values[140] * 140 + values[180] * 180 + values[220] * 220)

    println(sum)

    val image = List<CharArray>(6) { CharArray(40) { '.' } }

    for (y in 0 until 6) {
        for (x in 0 until 40) {
            val index = y * 40 + x + 1
            val spritePos = values[index]
            if (abs(x - spritePos) <= 1)
                image[y][x] = '#'
        }
    }

    for (line in image) {
        println(line)
    }
}