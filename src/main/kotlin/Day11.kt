import java.util.*

class Monkey(private val operationFun: (ULong) -> ULong, private val divisibilityValue: ULong,
             private val successMonkey: Int, private val failMonkey: Int, private var items: Queue<ULong>) {
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
}

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
//        Monkey({it * 19}, 23, 2, 3, LinkedList(listOf(79, 98))),
//        Monkey({it + 6}, 19, 2, 0, LinkedList(listOf(54, 65, 75, 74))),
//        Monkey({it * it}, 13, 1, 3, LinkedList(listOf(79, 60, 97))),
//        Monkey({it + 3}, 17, 0, 1, LinkedList(listOf(74))),
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
}