import java.util.*

class Monkey(private val operationFun: (ULong) -> ULong, private val divisibilityValue: ULong,
             private val successMonkey: Int, private val failMonkey: Int, private val items: Queue<ULong>) {
    var inspectCount: Int = 0
        private set

    private fun test(item: ULong) = item % divisibilityValue == 0UL

    private fun receiveItem(item: ULong) = items.add(item)

    fun inspectItems(monkeys: List<Monkey>) {
        while (!items.isEmpty()) {
            inspectCount++

            val item = items.remove()
            val worryItem = operationFun(item)
            val newItem = worryItem / 3UL
            if (test(newItem)) {
                monkeys[successMonkey].receiveItem(newItem)
            } else {
                monkeys[failMonkey].receiveItem(newItem)
            }
        }
    }

    fun inspectItems2(monkeys: List<Monkey>) {
        while (!items.isEmpty()) {
            inspectCount++

            val item = items.remove()
            val worryItem = operationFun(item)
            val newItem = worryItem % maxDivisor
            if (test(newItem)) {
                monkeys[successMonkey].receiveItem(newItem)
            } else {
                monkeys[failMonkey].receiveItem(newItem)
            }
        }
    }
}

val maxDivisor: ULong = 11UL * 2UL * 5UL * 17UL * 19UL * 7UL * 3UL * 13UL
//val maxDivisor: ULong = 23UL * 19UL * 13UL * 17UL

fun main() {
    val monkeys = listOf(
        Monkey({it * 5UL}, 11UL, 3, 4, LinkedList(listOf(92UL, 73UL, 86UL, 83UL, 65UL, 51UL, 55UL, 93UL))),
        Monkey({it * it}, 2UL, 6, 7, LinkedList(listOf(99UL, 67UL, 62UL, 61UL, 59UL, 98UL))),
        Monkey({it * 7UL}, 5UL, 1, 5, LinkedList(listOf(81UL, 89UL, 56UL, 61UL, 99UL))),
        Monkey({it + 1UL}, 17UL, 2, 5, LinkedList(listOf(97UL, 74UL, 68UL))),
        Monkey({it + 3UL}, 19UL, 2, 3, LinkedList(listOf(78UL, 73UL))),
        Monkey({it + 5UL}, 7UL, 1, 6, LinkedList(listOf(50UL))),
        Monkey({it + 8UL}, 3UL, 0, 7, LinkedList(listOf(95UL, 88UL, 53UL, 75UL))),
        Monkey({it + 2UL}, 13UL, 4, 0, LinkedList(listOf(50UL, 77UL, 98UL, 85UL, 94UL, 56UL, 89UL))),
    )

//    val monkeys = listOf(
//        Monkey({it * 19UL}, 23UL, 2, 3, LinkedList(listOf(79UL, 98UL))),
//        Monkey({it + 6UL}, 19UL, 2, 0, LinkedList(listOf(54UL, 65UL, 75UL, 74UL))),
//        Monkey({it * it}, 13UL, 1, 3, LinkedList(listOf(79UL, 60UL, 97UL))),
//        Monkey({it + 3UL}, 17UL, 0, 1, LinkedList(listOf(74UL))),
//    )

    repeat(20) {
        for (monkey in monkeys) {
            monkey.inspectItems(monkeys)
        }
    }

    val part1 = monkeys
        .map(Monkey::inspectCount)
        .sortedDescending()
        .take(2)
        .reduce { acc, it -> acc * it }

    println(part1)

    val monkeys2 = listOf(
        Monkey({it * 5UL}, 11UL, 3, 4, LinkedList(listOf(92UL, 73UL, 86UL, 83UL, 65UL, 51UL, 55UL, 93UL))),
        Monkey({it * it}, 2UL, 6, 7, LinkedList(listOf(99UL, 67UL, 62UL, 61UL, 59UL, 98UL))),
        Monkey({it * 7UL}, 5UL, 1, 5, LinkedList(listOf(81UL, 89UL, 56UL, 61UL, 99UL))),
        Monkey({it + 1UL}, 17UL, 2, 5, LinkedList(listOf(97UL, 74UL, 68UL))),
        Monkey({it + 3UL}, 19UL, 2, 3, LinkedList(listOf(78UL, 73UL))),
        Monkey({it + 5UL}, 7UL, 1, 6, LinkedList(listOf(50UL))),
        Monkey({it + 8UL}, 3UL, 0, 7, LinkedList(listOf(95UL, 88UL, 53UL, 75UL))),
        Monkey({it + 2UL}, 13UL, 4, 0, LinkedList(listOf(50UL, 77UL, 98UL, 85UL, 94UL, 56UL, 89UL))),
    )

//    val monkeys2 = listOf(
//        Monkey({it * 19UL}, 23UL, 2, 3, LinkedList(listOf(79UL, 98UL))),
//        Monkey({it + 6UL}, 19UL, 2, 0, LinkedList(listOf(54UL, 65UL, 75UL, 74UL))),
//        Monkey({it * it}, 13UL, 1, 3, LinkedList(listOf(79UL, 60UL, 97UL))),
//        Monkey({it + 3UL}, 17UL, 0, 1, LinkedList(listOf(74UL))),
//    )

    repeat(10000) {
        for (monkey in monkeys2) {
            monkey.inspectItems2(monkeys2)
        }
    }

    val part2Counts = monkeys2
        .map(Monkey::inspectCount)
        .sortedDescending()

    val part2 = part2Counts[0].toULong() * part2Counts[1].toULong()

    println(part2)
}