import java.io.File
import kotlin.math.min

abstract class Item(val name: String) {
    abstract fun getSize(): Int
    abstract fun part1(): Int
}

class Directory(name: String, val parent: Directory?) : Item(name) {
    private val children: MutableMap<String, Item> = mutableMapOf()

    override fun getSize() = children.values.sumOf { it.getSize() }

    override fun part1(): Int {
        var sum = children.values
            .filterIsInstance<Directory>()
            .map(Directory::getSize)
            .filter { it <= 100000 }
            .sum()
        sum += children.values.sumOf { it.part1() }
        return sum
    }

    fun part2(requiredSize: Int): Int {
        val propagatedResultMinimum = children.values
            .filterIsInstance<Directory>()
            .minOfOrNull { it.part2(requiredSize) }
            ?: Int.MAX_VALUE

        val directResultMinimum = children.values
            .filterIsInstance<Directory>()
            .map(Directory::getSize)
            .filter { it >= requiredSize }
            .minOrNull()
            ?: Int.MAX_VALUE

        return min(propagatedResultMinimum, directResultMinimum)
    }

    fun getChild(name: String): Directory {
        val i: Item? = children[name]
        if (i is Directory)
            return i

        throw IllegalStateException("Directory does not exist, or is a file: $name (from ${this.name})")
    }

    fun addItem(item: Item): Unit {
        children[item.name] = item
    }
}

class File(name: String, private val fileSize: Int) : Item(name) {
    override fun getSize() = fileSize
    override fun part1() = 0
}

val regCdX = """\$ cd ([a-z.]+)""".toRegex()
val regDirectory = """dir ([a-z]+)""".toRegex()
val regFile = """(\d+) ([a-z.]+)""".toRegex()

fun main() {
    val lines = File("./build/resources/main/day7.in").readLines()
    val rootDirectory = Directory("/", null)
    var currentDirectory = rootDirectory

    for (l in lines) {
        when (l) {
            "\$ cd .." -> { currentDirectory = currentDirectory.parent ?: throw IllegalStateException("trying to go up at root"); continue }
            "\$ cd /" -> { currentDirectory = rootDirectory ; continue }
            "\$ ls" -> { continue }
        }

        var res = regCdX.matchEntire(l)
        if (res != null) {
            val (name) = res.destructured
            currentDirectory = currentDirectory.getChild(name)
            continue
        }

        res = regDirectory.matchEntire(l)
        if (res != null) {
            val (name) = res.destructured
            val item = Directory(name, currentDirectory)
            currentDirectory.addItem(item)
            continue
        }

        res = regFile.matchEntire(l)
        if (res != null) {
            val (sizeStr, name) = res.destructured
            val size = sizeStr.toInt()
            val item = File(name, size)
            currentDirectory.addItem(item)
            continue
        }

        throw Exception("Unrecognized expression")
    }

    println(rootDirectory.part1())

    val totalSize = 70000000
    val rootSize = rootDirectory.getSize()
    val freeSpaceNeeded =  totalSize - rootSize

    println(rootDirectory.part2(freeSpaceNeeded))
}