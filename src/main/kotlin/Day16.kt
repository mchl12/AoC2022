import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.math.*

var valveMap: Map<String, Int> = mapOf()
var graph: List<MutableList<Int>> = listOf()
var flows: IntArray = IntArray(0)
var dists: List<IntArray> = listOf()
var valvesWithFlow: Int = 0
var memo: List<List<IntArray>> = listOf()

fun main() {
    val lines = File("./build/resources/main/day16.in").readLines()
    val regex = """Valve ([A-Z][A-Z]) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)""".toRegex()

    val regexedLines = lines.map { regex.matchEntire(it)!! }

    valveMap = regexedLines
        .map { match -> Pair(match.groupValues[2].toInt(), match.groupValues[1]) }
        .sortedByDescending { it.first }
        .mapIndexed { i, p -> Pair(p.second, i) }
        .associate { it }

    println(valveMap)

    graph = List(valveMap.size) { mutableListOf() }
    flows = IntArray(valveMap.size) { 0 }

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

    dists = List(valveMap.size) { IntArray(valveMap.size) { -1 } }
    val queue: Queue<Int> = LinkedList()

    for (i in valveMap.values) {
        val distArray = dists[i]
        queue.add(i)
        distArray[i] = 0
        while (!queue.isEmpty()) {
            val j = queue.remove()
            val dist = distArray[j]
            for (n in graph[j]) {
                if (distArray[n] >= 0)
                    continue
                queue.add(n)
                distArray[n] = dist + 1
            }
        }
    }

    valvesWithFlow = flows.indexOfFirst { it == 0 }

    memo = List(31) { List(valveMap.size) { IntArray(1 shl valvesWithFlow) { -1 } } }

    var result = 0
    val start = valveMap["AA"]!!
    val ms = measureTimeMillis {
        result = part1(30, start, 0)
    }
    println(result)
    println("$ms ms")

    var part2 = 0
    for (i in 0 until (1 shl valvesWithFlow)) {
        val score1 = part1(26, start, i)
        val score2 = part1(26, start, i xor ((1 shl valvesWithFlow) - 1))
        part2 = max(part2, score1 + score2)
    }

    println(part2)
}

fun part1(minutes: Int, position: Int, valveBitmap: Int): Int {
    if (memo[minutes][position][valveBitmap] >= 0)
        return memo[minutes][position][valveBitmap]

    var result = 0

    for (i in 0 until valvesWithFlow) {
        if ((valveBitmap and (1 shl i)) != 0)
            continue
        val time = dists[position][i] + 1
        val newMinutes = minutes - time
        if (newMinutes <= 0)
            continue
        val newPosition = i
        val newValveBitmap = valveBitmap or (1 shl i)
        val pressureRelease = newMinutes * flows[i]
        val score = part1(newMinutes, newPosition, newValveBitmap) + pressureRelease
        if (score > result)
            result = score
    }

    memo[minutes][position][valveBitmap] = result
    return result
}