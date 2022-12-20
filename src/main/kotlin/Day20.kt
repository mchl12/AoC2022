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
            for (j in newPos + 1..curPos) {
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
}