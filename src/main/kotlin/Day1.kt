import java.io.File

fun main() {
    val inputLines: List<String> = File("""D:\IdeaProjects\AoC2022\src\main\resources\day1.in""").readLines()

    val totalCalories: MutableList<Int> = mutableListOf()
    totalCalories.add(0)

    for (line: String in inputLines) {
        if (line.isEmpty()) {
            totalCalories.add(0)
            continue
        }

        val v: Int = line.toInt()
        totalCalories[totalCalories.lastIndex] += v
    }

    // day 2
    val totalSortedCalories: List<Int> = totalCalories.sortedDescending()
    val total: Int = totalSortedCalories[0] + totalSortedCalories[1] + totalSortedCalories[2]
    println(total)

    // day 1
    //println(totalCalories.max())
}