import java.io.File

val memo: List<List<IntArray>> = listOf() // minutes, position, open valves

fun main() {
    val lines = File("./build/resources/main/day16.in").readLines()
    val regex = """Valve ([A-Z][A-Z]) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)""".toRegex()

    val regexedLines = lines.map { regex.matchEntire(it)!! }

    val valveMap = regexedLines
        .mapIndexed { index, match -> Triple(match.groupValues[2].toInt(), match.groupValues[1], index) }
        .sortedByDescending { it.first }
        .map { Pair(it.second, it.third) }
        .associate { it }

    println(valveMap)

    val graph = List(valveMap.size) { mutableListOf<Int>() }
    val flows = IntArray(valveMap.size) { 0 }

    for ((name, flowStr, tunnelList) in regexedLines.map(MatchResult::destructured)) {
        val index = valveMap[name]!!
        val flow = flowStr.toInt()
        val tunnels = tunnelList.split(", ")

        for (tunnel in tunnels) {
            val otherIndex = valveMap[tunnel]!!
            graph[index].add(otherIndex)
        }

        flows[index] = flow
    }

    // bfs for each node to get distance from any node to any other node
    // calculate number of valves with a flow
    // initialize the memo by [minutesLeft][currentLocation][openedValves]
    // where openedValves is a bitmap that has a bit for each valve with a flow rate > 0
    // should result in at most 30 * 14 * 2^14 states (14 is number of valves with flow rate in real input)
    // with top down we don't even visit all
    // calculating one state is just a comparison between all 14 possible unvisited states
    // top down function returns the total flow rate
}