import java.io.File

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

    println("${values[20]} ${values[60]} ${values[100]} ${values[140]} ${values[180]} ${values[220]}")

    println(sum)
}