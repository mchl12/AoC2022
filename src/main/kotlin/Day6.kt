import java.io.File
import kotlin.system.measureTimeMillis

fun getFirstDistinctN(codeInt: IntArray, n: Int): Int {
    val letterIncluded = IntArray(26) { 0 }
    var counter = 0
    for (i in 0 until n) {
        val v = codeInt[i]
        if (letterIncluded[v] == 0)
            counter++
        letterIncluded[v]++
    }
    var i = n
    while (counter < n) {
        val v = codeInt[i - n]

        when (letterIncluded[v]) {
            0 -> {}
            1 -> {
                counter--
                letterIncluded[v] = 0
            }
            else -> letterIncluded[v]--
        }

        val w = codeInt[i]
        if (letterIncluded[w] == 0)
            counter++

        letterIncluded[w]++

        i++
    }

    return i
}

fun main() {
    val lines = File("./build/resources/main/day6.in").readLines()

    val code = lines[0]

    // first slow version
//    val indexp1 = code
//        .windowedSequence(4)
//        .indexOfFirst { it.toSet().size == 4 }
//
//    println(indexp1 + 4)
//
//    val indexp2 = code
//        .windowedSequence(14)
//        .indexOfFirst { it.toSet().size == 14 }
//
//    println(indexp2 + 14)

    // fast version
    val time = measureTimeMillis {
        val codeInt: IntArray = code.map { c -> c - 'a' }.toIntArray()

        println(getFirstDistinctN(codeInt, 4))
        println(getFirstDistinctN(codeInt, 14))
    }
    println("time = ${time}ms")
}