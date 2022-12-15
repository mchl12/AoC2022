import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Sensor(val x: Int, val y: Int, val closestBeaconX: Int, val closestBeaconY: Int) {
    val dist = abs(closestBeaconX - x) + abs(closestBeaconY - y)
}

data class Range(val l: Int, val r: Int)

fun Range.canCombine(other: Range): Boolean {
    return l <= other.r && other.l <= r
}

fun Range.combine(other: Range): Range {
    return Range(min(l, other.l), max(r, other.r))
}

fun main() {
    val lines = File("./build/resources/main/day15.in").readLines()
    val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()

    val targetY = 2000000
    val maxVal = 4000000
//    val targetY = 10
//    val maxVal = 20

    val sensors = lines
        .map { regex.matchEntire(it)!! }
        .map {
            val (v1, v2, v3, v4) = it.destructured
            Sensor(v1.toInt(), v2.toInt(), v3.toInt(), v4.toInt())
        }

    val minX = sensors
        .minOf { it.x - it.dist }

    val sensorsMoved = sensors
        .map { it.copy(x = it.x - minX, closestBeaconX = it.closestBeaconX - minX) }

    val maxX = sensorsMoved
        .maxOf { it.x + it.dist }

    val impossibleLocations = BooleanArray(maxX + 1) { false }

    for (sensor in sensorsMoved) {
        val yDist = abs(sensor.y - targetY)
        val dist = sensor.dist - yDist
        val xLow = sensor.x - dist
        val xHigh = sensor.x + dist
        for (x in xLow .. xHigh) {
            impossibleLocations[x] = true
        }
    }

    for (sensor in sensorsMoved) {
        if (sensor.closestBeaconY == targetY) {
            impossibleLocations[sensor.closestBeaconX] = false
        }
    }

    val part1 = impossibleLocations
        .count { it }

    println(part1)

    val ranges = mutableListOf<Range>()
    var answerX = 0
    var answerY = 0
    for (y in 0..maxVal) {
        ranges.clear()

        for (sensor in sensors) {
            val yDist = abs(sensor.y - y)
            val dist = sensor.dist - yDist
            if (dist < 0)
                continue

            val xLow = sensor.x - dist
            val xHigh = sensor.x + dist
            val l = max(xLow, 0)
            val h = min(xHigh, maxVal)

            var range = Range(l, h)
            var i = 0
            while (i < ranges.size) {
                if (range.canCombine(ranges[i])) {
                    range = range.combine(ranges[i])
                    ranges.removeAt(i)
                } else {
                    i++
                }
            }
            ranges.add(range)
        }

        assert(ranges.size in 1..2)
        if (ranges.size == 2) {
            val range = ranges[0]
            if (range.l == 0) {
                assert(ranges[1].l == range.r + 2)
                answerX = range.r + 1
            } else {
                assert (range.r == maxVal)
                assert(ranges[1].r == range.l - 2)
                answerX = range.l - 1
            }
            answerY = y
            break
        } else {
            val range = ranges[0]
            if (range.l != 0) {
                assert(range.l == 1)
                answerX = 0
                answerY = y
                break
            }

            if (range.r != maxVal) {
                assert(range.r == maxVal - 1)
                answerX = maxVal
                answerY = y
                break
            }
        }
    }

    val part2 = answerX.toLong() * 4000000L + answerY.toLong()
    println(part2)
}