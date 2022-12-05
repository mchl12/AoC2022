import java.io.File
import java.util.*

data class Instruction(val count: Int, val origin: Int, val target: Int)

fun main() {
    val regexCrate = """(?:   |\[([A-Z])\]) ?""".toRegex()
    val regexNumbers = """ \d[\d ]+""".toRegex()
    val regexInstruction = """move (\d+) from (\d) to (\d)""".toRegex()

    val lines = File("./build/resources/main/day5.in").readLines()

    val crateLines = lines
        .takeWhile { s -> !regexNumbers.matches(s) }
        .map { s ->
        regexCrate.findAll(s)
            .map { val (v) = it.destructured; if (v.isEmpty()) ' ' else v[0] }
            .toList()
    }

    val instructions = lines
        .dropWhile { s -> !regexNumbers.matches(s) }
        .drop(2)
        .map { regexInstruction.matchEntire(it) }
        .map {
            val (count, orig, target) = it!!.destructured
            Instruction(count.toInt(), orig.toInt() - 1, target.toInt() - 1)
        }

    val crates: Array<Stack<Char>> = arrayOf(Stack(), Stack(), Stack(),
        Stack(), Stack(), Stack(),
        Stack(), Stack(), Stack())

    for (l in crateLines.reversed()) {
        for (i in l.indices) {
            if (l[i] != ' ') {
                crates[i].add(l[i])
            }
        }
    }

    // part 1
//    for (instr in instructions) {
//        repeat(instr.count) {
//            crates[instr.target].add(crates[instr.origin].pop())
//        }
//    }

    // part 2
    val tempStack: Stack<Char> = Stack<Char>()
    for (instr in instructions) {
        repeat(instr.count) {
            tempStack.add(crates[instr.origin].pop())
        }
        repeat(instr.count) {
            crates[instr.target].add(tempStack.pop())
        }
    }

    for (s in crates) {
        if (s.empty()){
            print(' ')}
        else
            print(s.peek())
    }
    println()
}