import java.io.File
import kotlin.math.abs
import kotlin.math.max

data class D9Instruction(val direction: Vector2, val length: Int)

fun instructionFromString(str: String): D9Instruction {
    val (d, l) = str.split(' ')
    val dir = directionFromChar(d[0])
    return D9Instruction(dir, l.toInt())
}

data class Vector2(val x: Int, val y: Int)

fun directionFromChar(c: Char): Vector2 {
    return when (c) {
        'R' -> Vector2(1, 0)
        'L' -> Vector2(-1, 0)
        'U' -> Vector2(0, 1)
        'D' -> Vector2(0, -1)
        else -> throw IllegalArgumentException("invalid direction char")
    }
}

operator fun Vector2.plus(other: Vector2): Vector2 {
    return Vector2(x + other.x, y + other.y)
}

operator fun Vector2.minus(other: Vector2) = Vector2(x - other.x, y - other.y)

fun dist(a: Vector2, b: Vector2) = max(
    abs(a.x - b.x),
    abs(a.y - b.y)
)

fun algorithm(instructions: List<D9Instruction>, length: Int): Int {
    val visited: MutableSet<Vector2> = mutableSetOf()

    val knots: Array<Vector2> = Array(length) { Vector2(0, 0) }
    visited.add(knots.last())

    for (instr in instructions) {
        val dir = instr.direction
        for (i in 1..instr.length) {
            knots[0] += dir
            for (j in 1 until knots.size) {
                val head = knots[j-1]
                var tail = knots[j]

                val dist = dist(head, tail)
                if (dist > 2)
                    throw Exception()
                if (dist < 2)
                    continue

                val diff = head - tail
                if (diff.x != 0) {
                    tail += Vector2(diff.x / abs(diff.x), 0)
                }
                if (diff.y != 0) {
                    tail += Vector2(0, diff.y / abs(diff.y))
                }

                knots[j] = tail
            }

            visited.add(knots.last())
        }
    }

    return visited.size
}

fun main() {
    val lines = File("./build/resources/main/day9.in").readLines()

    val instructions = lines.map { instructionFromString(it) }

    println(algorithm(instructions, 2))
    println(algorithm(instructions, 10))
}