package day25

import java.io.File

fun main() {
    val lines = File("./build/resources/main/day25.in").readLines()
    val sum = lines.sumOf { snafuToDecimal(it) }
    println(sum)
    println(decimalToSnafu(sum))
}

fun decimalToSnafu(decimalValue: Long): String {
    var n = decimalValue
    val digitList = mutableListOf<Int>()
    while (n > 0) {
        n += 2
        val div = n / 5
        val mod = n.mod(5)
        digitList.add(mod)
        n = div
    }
    return digitList.map { it - 2 }.map { decimalDigitToSnafuDigit(it) }.reversed().joinToString(separator = "")
}

fun snafuToDecimal(snafuNum: String): Long {
    var multiplier = 1L
    var result = 0L
    for (c in snafuNum.reversed()) {
        result += multiplier * snafuDigitToDecimalDigit(c)
        multiplier *= 5L
    }
    return result
}

fun snafuDigitToDecimalDigit(c: Char) = when (c) {
    '0', '1', '2' -> (c - '0').toLong()
    '-' -> -1L
    '=' -> -2L
    else -> throw IllegalArgumentException()
}

fun decimalDigitToSnafuDigit(d: Int) = when (d) {
    -2 -> '='
    -1 -> '-'
    0 -> '0'
    1 -> '1'
    2 -> '2'
    else -> throw IllegalArgumentException()
}
