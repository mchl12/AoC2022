import java.io.File

interface IMonkey {
    val result: Long
}

abstract class BinaryOperationMonkey(val a: IMonkey, val b: IMonkey) : IMonkey {
    override val result: Long by lazy {
        performOperation()
    }

    abstract fun performOperation(): Long
}

class PlusMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation() = a.result + b.result
}

class MinusMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation() = a.result - b.result
}

class TimesMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation() = a.result * b.result
}

class DivideMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation(): Long {
        if (a.result % b.result != 0L)
            println("Warning: non-integer division")
        return a.result / b.result
    }
}

class ValueMonkey(override val result: Long) : IMonkey

val regexPlus = """[a-z]{4}: ([a-z]{4}) \+ ([a-z]{4})""".toRegex()
val regexMinus = """[a-z]{4}: ([a-z]{4}) - ([a-z]{4})""".toRegex()
val regexTimes = """[a-z]{4}: ([a-z]{4}) \* ([a-z]{4})""".toRegex()
val regexDivide = """[a-z]{4}: ([a-z]{4}) / ([a-z]{4})""".toRegex()
val regexNum = """[a-z]{4}: (\d+)""".toRegex()

fun stringToMonkey(monkeys: Array<IMonkey?>, lines: List<String>, nameToIndex: Map<String, Int>, i: Int) {
    val str = lines[i]

    var match: MatchResult?
    match = regexPlus.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkey(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkey(monkeys, lines, nameToIndex, b)

        monkeys[i] = PlusMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexMinus.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkey(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkey(monkeys, lines, nameToIndex, b)

        monkeys[i] = MinusMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexTimes.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkey(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkey(monkeys, lines, nameToIndex, b)

        monkeys[i] = TimesMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexDivide.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkey(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkey(monkeys, lines, nameToIndex, b)

        monkeys[i] = DivideMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexNum.matchEntire(str)
    if (match != null) {
        val v = match.groupValues[1].toLong()
        monkeys[i] = ValueMonkey(v)
        return
    }

    throw Exception("Invalid monkey string, did not match anything: $str")
}

fun main() {

    val lines = File("./build/resources/main/day21.in").readLines()

    val nameToIndex = lines
        .map { it.substringBefore(':') }
        .mapIndexed { index, s -> Pair(s, index) }
        .associate { it }

    assert(nameToIndex.size == lines.size)

    val monkeys = Array<IMonkey?>(lines.size) { null }
    stringToMonkey(monkeys, lines, nameToIndex, nameToIndex["root"]!!)

    val part1 = monkeys[nameToIndex["root"]!!]!!.result
    println(part1)
}