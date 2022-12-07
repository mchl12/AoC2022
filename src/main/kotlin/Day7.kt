import java.io.File

abstract class Item(val name: String) {
    abstract fun getSize(): Int
}

class Directory(name: String, val parent: Directory?) : Item(name) {
    private val children: MutableMap<String, Item> = mutableMapOf()

    override fun getSize() = children.values.sumOf { it.getSize() }

    fun getDirectorySizes(): List<Int> {
        val subDirectorySizes: List<List<Int>> = children.values
            .filterIsInstance<Directory>()
            .map(Directory::getDirectorySizes)

        val ownSizeFiles: Int = children.values
            .filterIsInstance<AOCFile>()
            .sumOf(AOCFile::getSize)
        val ownSize = subDirectorySizes.sumOf(List<Int>::last) + ownSizeFiles

        val combinedList = subDirectorySizes.reduceOrNull { acc, list -> acc + list }

        if (combinedList == null) {
            return listOf(ownSize)
        }

        return combinedList + ownSize
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

class AOCFile(name: String, private val fileSize: Int) : Item(name) {
    override fun getSize() = fileSize
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
            val item = AOCFile(name, size)
            currentDirectory.addItem(item)
            continue
        }

        throw Exception("Unrecognized expression")
    }

    val directorySizes = rootDirectory.getDirectorySizes()

    val part1 = directorySizes
        .filter { it <= 100000 }
        .sum()
    println(part1)

    val totalSize = 70000000
    val neededSpace = 30000000

    val rootDirectorySize = directorySizes.last()
    val spaceLeft = totalSize - rootDirectorySize
    val requiredSpace = neededSpace - spaceLeft

    val part2 = directorySizes
        .filter { it >= requiredSpace }
        .min()
    println(part2)
}