import java.io.File

interface IMonkey {
    val result: Long?

    fun matchTarget(target: Long): Long
}

abstract class BinaryOperationMonkey(val a: IMonkey, val b: IMonkey) : IMonkey {
    override val result: Long? by lazy(fun(): Long? {
        val va = a.result
        val vb = b.result
        if (va == null || vb == null)
            return null
        return performOperation(va, vb)
    })

    abstract fun performOperation(va: Long, vb: Long): Long
}

class PlusMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation(va: Long, vb: Long): Long = va + vb

    override fun matchTarget(target: Long): Long {
        if (a.result == null)
            return a.matchTarget(target - b.result!!)
        if (b.result == null)
            return b.matchTarget(target - a.result!!)
        throw Exception("one operand must be null")
    }
}

class MinusMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation(va: Long, vb: Long): Long = va - vb

    override fun matchTarget(target: Long): Long {
        if (a.result == null)
            return a.matchTarget(target + b.result!!)
        if (b.result == null)
            return b.matchTarget(a.result!! - target)
        throw Exception("one operand must be null")
    }
}

class TimesMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation(va: Long, vb: Long): Long = va * vb

    override fun matchTarget(target: Long): Long {
        if (a.result == null)
            return a.matchTarget(target / b.result!!)
        if (b.result == null)
            return b.matchTarget(target / a.result!!)
        throw Exception("one operand must be null")
    }
}

class DivideMonkey(a: IMonkey, b: IMonkey) : BinaryOperationMonkey(a, b)
{
    override fun performOperation(va: Long, vb: Long): Long {
        if (va % vb != 0L)
            println("Warning: non-integer division")
        return va / vb
    }

    override fun matchTarget(target: Long): Long {
        if (a.result == null)
            return a.matchTarget(target * b.result!!)
        if (b.result == null)
            return b.matchTarget(a.result!! / target)
        throw Exception("one operand must be null")
    }
}

class ValueMonkey(override val result: Long) : IMonkey {
    override fun matchTarget(target: Long): Long {
        throw Exception("cannot match target")
    }
}

class HumanMonkey() : IMonkey {
    override val result: Long?
        get() = null

    override fun matchTarget(target: Long): Long {
        return target
    }
}

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

fun stringToMonkeyP2(monkeys: Array<IMonkey?>, lines: List<String>, nameToIndex: Map<String, Int>, i: Int) {
    val str = lines[i]

    var match: MatchResult?
    match = regexPlus.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, b)

        monkeys[i] = PlusMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexMinus.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, b)

        monkeys[i] = MinusMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexTimes.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, b)

        monkeys[i] = TimesMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexDivide.matchEntire(str)
    if (match != null) {
        val (nameA, nameB) = match.destructured
        val a = nameToIndex[nameA]!!
        val b = nameToIndex[nameB]!!

        if (monkeys[a] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, a)
        if (monkeys[b] == null)
            stringToMonkeyP2(monkeys, lines, nameToIndex, b)

        monkeys[i] = DivideMonkey(monkeys[a]!!, monkeys[b]!!)
        return
    }

    match = regexNum.matchEntire(str)
    if (match != null) {
        if (str.substringBefore(':') == "humn")
            monkeys[i] = HumanMonkey()
        else {
            val v = match.groupValues[1].toLong()
            monkeys[i] = ValueMonkey(v)
        }
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

    val monkeysP2 = Array<IMonkey?>(lines.size) { null }
    stringToMonkeyP2(monkeysP2, lines, nameToIndex, nameToIndex["root"]!!)

    val rootIMonkey = monkeysP2[nameToIndex["root"]!!]
    if (rootIMonkey !is BinaryOperationMonkey) {
        throw Exception("root must be binary operation")
    } else {
        val resA = rootIMonkey.a.result
        val resB = rootIMonkey.b.result
        if (resA != null) {
            println(rootIMonkey.b.matchTarget(resA))
        }
        else if (resB != null) {
            println(rootIMonkey.a.matchTarget(resB))
        }
        else
            throw Exception("one monkey result must be null")
    }
}