package day23

import java.io.File

val orientationOrder = listOf(Orientation.Top, Orientation.Bottom, Orientation.Left, Orientation.Right)
val surroundingDirections = listOf(
    Vec2(1, -1),
    Vec2(1, 0),
    Vec2(1, 1),
    Vec2(0, -1),
    Vec2(0, 1),
    Vec2(-1, -1),
    Vec2(-1, 0),
    Vec2(-1, 1),
)

fun main() {
    val lines = File("./build/resources/main/day23.in").readLines()
    val mutElves = mutableListOf<Elf>()
    for (i in lines.indices) {
        val line = lines[i]
        mutElves.addAll(
            line.withIndex()
                .filter { it.value == '#' }
                .map { Elf(Vec2(it.index, i)) }
        )
    }

    val elves = mutElves.toList()
    val elfPositions = elves.associateBy { it.pos }.toMutableMap()

    // part 1
    //repeat(10) {
    //    planElves(elves, elfPositions)
    //    elfPositions.clear()
    //    for (elf in elves) {
    //        elf.addNewPos(elfPositions)
    //    }
    //    for (elf in elves) {
    //        elf.pos = elf.newPos
    //        elf.directionStart = (elf.directionStart + 1) % orientationOrder.size
    //    }
    //}
//
    //val minX = elves.minOf { it.pos.x }
    //val maxX = elves.maxOf { it.pos.x }
    //val minY = elves.minOf { it.pos.y }
    //val maxY = elves.maxOf { it.pos.y }
    //val part1 = (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    //println(part1)

    // part 2
    var i = 0
    while (true) {
        i++
        planElves(elves, elfPositions)
        elfPositions.clear()
        for (elf in elves) {
            elf.addNewPos(elfPositions)
        }

        var moved = false
        for (elf in elves) {
            if (elf.pos != elf.newPos)
                moved = true
            elf.pos = elf.newPos
            elf.directionStart = (elf.directionStart + 1) % orientationOrder.size
        }

        if (!moved)
            break
    }
    println(i)
}

fun printElves(elves: List<Elf>) {
    val minX = elves.minOf { it.pos.x }
    val maxX = elves.maxOf { it.pos.x }
    val minY = elves.minOf { it.pos.y }
    val maxY = elves.maxOf { it.pos.y }

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            if (elves.find { it.pos == Vec2(x, y) } != null) {
                print('#')
            } else
                print('.')
        }
        println()
    }
    println()
}

fun planElves(elves: List<Elf>, elfPositions: Map<Vec2, Elf>) {
    for (elf in elves) {
        if (!elf.hasNeighbour(elfPositions))
            continue

        elf.planNewPos(elfPositions)
    }
}

fun directionEmpty(elfPositions: Map<Vec2, Elf>, position: Vec2, orientation: Orientation): Boolean {
    if (elfPositions.containsKey(position + orientation.diff))
        return false

    if (elfPositions.containsKey(position + orientation.diff + orientation.getLeft().diff))
        return false

    if (elfPositions.containsKey(position + orientation.diff + orientation.getRight().diff))
        return false

    return true
}

data class Elf(var pos: Vec2) {
    var newPos = pos
    var directionStart = 0

    fun hasNeighbour(elfPositions: Map<Vec2, Elf>): Boolean {
        for (dir in surroundingDirections) {
            val checkPos = pos + dir
            if (elfPositions.containsKey(checkPos))
                return true
        }

        return false
    }

    fun planNewPos(elfPositions: Map<Vec2, Elf>) {
        for (i in orientationOrder.indices) {
            val orientation = orientationOrder[(directionStart + i) % orientationOrder.size]
            if (directionEmpty(elfPositions, pos, orientation)) {
                newPos = pos + orientation.diff
                return
            }
        }
    }

    fun addNewPos(elfPositions: MutableMap<Vec2, Elf>) {
        val otherElf = elfPositions.remove(newPos)
        if (otherElf != null) {
            newPos = pos
            otherElf.newPos = otherElf.pos
            addNewPos(elfPositions)
            otherElf.addNewPos(elfPositions)
        } else {
            elfPositions[newPos] = this
        }
    }
}

enum class Orientation (val diff: Vec2) {
    Left(Vec2(-1, 0)),
    Right(Vec2(1, 0)),
    Top(Vec2(0, -1)),
    Bottom(Vec2(0, 1));

    fun getOpposite() = when (this) {
        Left -> Right
        Right -> Left
        Top -> Bottom
        Bottom -> Top
    }

    fun getRight() = when (this) {
        Left -> Top
        Top -> Right
        Right -> Bottom
        Bottom -> Left
    }

    fun getLeft() = when (this) {
        Left -> Bottom
        Bottom -> Right
        Right -> Top
        Top -> Left
    }
}

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(that: Vec2) = Vec2(x + that.x, y + that.y)
    operator fun minus(that: Vec2) = Vec2(x - that.x, y - that.y)
    operator fun unaryMinus() = Vec2(-x, -y)
    operator fun times(m: Int) = Vec2(x * m, y * m)
}
