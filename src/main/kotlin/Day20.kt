import java.io.File

fun main() {
    val lines = File("./build/resources/main/day20.in").readLines()
    val integers = lines.map(String::toInt).toMutableList() // maps current to the value

    val originalPositions = integers.indices.toList().toIntArray() // maps current to original
    val currentPositions = integers.indices.toList().toIntArray() // maps original to current

    for (i in currentPositions.indices) {
        val curPos = currentPositions[i]
        val value = integers.removeAt(curPos)
        val newPos = (curPos + value).mod(integers.size)
        integers.add(newPos, value)

        if (newPos < curPos) {
            for (j in curPos downTo newPos + 1) {
                val originalPos = originalPositions[j - 1]
                originalPositions[j] = originalPos
                currentPositions[originalPos] = j
            }
        } else {
            for (j in curPos until newPos) {
                val originalPos = originalPositions[j + 1]
                originalPositions[j] = originalPos
                currentPositions[originalPos] = j
            }
        }

        originalPositions[newPos] = i
        currentPositions[i] = newPos
    }

    val index0 = integers.indexOf(0)
    val part1 = listOf(1000, 2000, 3000)
        .map { (it + index0).mod(integers.size) }
        .sumOf { integers[it] }
    println(part1)

    val decryptionKey = 811589153L
    val integersP2 = lines.map(String::toLong).map { it * decryptionKey }.toMutableList() // maps current to the value

    for (i in integersP2.indices) {
        originalPositions[i] = i
        currentPositions[i] = i
    }

    repeat(10) {
        for (i in currentPositions.indices) {
            val curPos = currentPositions[i]
            val value = integersP2.removeAt(curPos)
            val newPos = (curPos + value).mod(integersP2.size)
            integersP2.add(newPos, value)

            if (newPos < curPos) {
                for (j in curPos downTo newPos + 1) {
                    val originalPos = originalPositions[j - 1]
                    originalPositions[j] = originalPos
                    currentPositions[originalPos] = j
                }
            } else {
                for (j in curPos until newPos) {
                    val originalPos = originalPositions[j + 1]
                    originalPositions[j] = originalPos
                    currentPositions[originalPos] = j
                }
            }

            originalPositions[newPos] = i
            currentPositions[i] = newPos
        }
    }

    val index0p2 = integersP2.indexOf(0)
    val part2 = listOf(1000, 2000, 3000)
        .map { (it + index0p2).mod(integersP2.size) }
        .sumOf { integersP2[it] }
    println(part2)
}