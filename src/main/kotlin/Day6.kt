import java.io.File

fun main() {
    val lines = File("./build/resources/main/day6.in").readLines()

    val code = lines[0]

    val indexp1 = code
        .windowedSequence(4)
        .indexOfFirst { it.toSet().size == 4 }

    println(indexp1 + 4)

    val indexp2 = code
        .windowedSequence(14)
        .indexOfFirst { it.toSet().size == 14 }

    println(indexp2 + 14)
}