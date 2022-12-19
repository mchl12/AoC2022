import java.io.File
import kotlin.math.*

fun main() {
    val sol = Day19()
    sol.part1()
    sol.part2()
}

data class RobotState(val oreGeneration: Int, val ore: Int, val clayGeneration: Int, val clay: Int
        , val obsidianGeneration: Int, val obsidian:Int) {

    private fun timeToCollect(robotCost: RobotCost): Int? {
        var maxTime = 0
        if (ore < robotCost.ore) {
            if (oreGeneration == 0)
                return null

            val time = ceil((robotCost.ore - ore) / oreGeneration.toFloat()).toInt()
            maxTime = max(maxTime, time)
        }
        if (clay < robotCost.clay) {
            if (clayGeneration == 0)
                return null

            val time = ceil((robotCost.clay - clay) / clayGeneration.toFloat()).toInt()
            maxTime = max(maxTime, time)
        }
        if (obsidian < robotCost.obsidian) {
            if (obsidianGeneration == 0)
                return null

            val time = ceil((robotCost.obsidian - obsidian) / obsidianGeneration.toFloat()).toInt()
            maxTime = max(maxTime, time)
        }
        return maxTime
    }

    fun buildOre(blueprint: RobotBlueprint): Pair<Int, RobotState>? {
        val cost = blueprint.oreRobotCost
        val timeToCollect = timeToCollect(cost) ?: return null
        val time = timeToCollect + 1
        val newOre = ore + oreGeneration * time - cost.ore
        val newClay = clay + clayGeneration * time - cost.clay
        val newObsidian = obsidian + obsidianGeneration * time - cost.obsidian
        return Pair(time, copy(oreGeneration = oreGeneration + 1, ore = newOre,
        clay = newClay, obsidian = newObsidian))
    }

    fun buildClay(blueprint: RobotBlueprint): Pair<Int, RobotState>? {
        val cost = blueprint.clayRobotCost
        val timeToCollect = timeToCollect(cost) ?: return null
        val time = timeToCollect + 1
        val newOre = ore + oreGeneration * time - cost.ore
        val newClay = clay + clayGeneration * time - cost.clay
        val newObsidian = obsidian + obsidianGeneration * time - cost.obsidian
        return Pair(time, copy(clayGeneration = clayGeneration + 1, ore = newOre,
            clay = newClay, obsidian = newObsidian))
    }

    fun buildObsidian(blueprint: RobotBlueprint): Pair<Int, RobotState>? {
        val cost = blueprint.obsidianRobotCost
        val timeToCollect = timeToCollect(cost) ?: return null
        val time = timeToCollect + 1
        val newOre = ore + oreGeneration * time - cost.ore
        val newClay = clay + clayGeneration * time - cost.clay
        val newObsidian = obsidian + obsidianGeneration * time - cost.obsidian
        return Pair(time, copy(obsidianGeneration = obsidianGeneration + 1, ore = newOre,
            clay = newClay, obsidian = newObsidian))
    }

    fun buildGeode(blueprint: RobotBlueprint): Pair<Int, RobotState>? {
        val cost = blueprint.geodeRobotCost
        val timeToCollect = timeToCollect(cost) ?: return null
        val time = timeToCollect + 1
        val newOre = ore + oreGeneration * time - cost.ore
        val newClay = clay + clayGeneration * time - cost.clay
        val newObsidian = obsidian + obsidianGeneration * time - cost.obsidian
        return Pair(time, copy(ore = newOre,
            clay = newClay, obsidian = newObsidian))
    }
}

data class RobotBlueprint(val oreRobotCost: RobotCost, val clayRobotCost: RobotCost
        , val obsidianRobotCost: RobotCost, val geodeRobotCost: RobotCost) {
    val maxOreCost = listOf(clayRobotCost.ore, obsidianRobotCost.ore, geodeRobotCost.ore).max()
    val maxClayCost = obsidianRobotCost.clay
    val maxObsidianCost = geodeRobotCost.obsidian
}

data class RobotCost(val ore: Int, val clay: Int, val obsidian: Int)

class Day19 {
    private val blueprints: List<RobotBlueprint>

    init {
        val regex = """Blueprint \d+: Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
        val lines = File("./build/resources/main/day19.in").readLines()
        blueprints = lines
            .map { regex.matchEntire(it) ?: throw Exception("$it failed to match") }
            .map { it.destructured }
            .map { (oreore, clayore, obsidianore, obsidianclay, geodeore, geodeobsidian) ->
                RobotBlueprint(
                    RobotCost(oreore.toInt(), 0, 0),
                    RobotCost(clayore.toInt(), 0, 0),
                    RobotCost(obsidianore.toInt(), obsidianclay.toInt(), 0),
                    RobotCost(geodeore.toInt(), 0, geodeobsidian.toInt())
                )
            }
    }

    private fun scoreBlueprint(blueprint: RobotBlueprint, minutesLeft: Int, state: RobotState): Int {
        if (minutesLeft == 0)
            return 0

        var maxScore = 0

        var p: Pair<Int, RobotState>?

        if (state.ore + state.oreGeneration * minutesLeft < blueprint.maxOreCost * minutesLeft) {
            p = state.buildOre(blueprint)
            if (p != null && p.first <= minutesLeft) {
                val time = p.first
                val newState = p.second
                maxScore = max(maxScore, scoreBlueprint(blueprint, minutesLeft - time, newState))
            }
        }

        if (state.clay + state.clayGeneration * minutesLeft < blueprint.maxClayCost * minutesLeft) {
            p = state.buildClay(blueprint)
            if (p != null && p.first <= minutesLeft) {
                val time = p.first
                val newState = p.second
                maxScore = max(maxScore, scoreBlueprint(blueprint, minutesLeft - time, newState))
            }
        }

        if (state.obsidian + state.obsidianGeneration * minutesLeft < blueprint.maxObsidianCost * minutesLeft) {
            p = state.buildObsidian(blueprint)
            if (p != null && p.first <= minutesLeft) {
                val time = p.first
                val newState = p.second
                maxScore = max(maxScore, scoreBlueprint(blueprint, minutesLeft - time, newState))
            }
        }

        p = state.buildGeode(blueprint)
        if (p != null && p.first <= minutesLeft) {
            val time = p.first
            val newState = p.second
            maxScore = max(maxScore, scoreBlueprint(blueprint, minutesLeft - time, newState) + minutesLeft - time)
        }

        return maxScore
    }

    fun part1() {
        val defaultState = RobotState(1, 0, 0, 0, 0, 0)

        val score = blueprints
            .mapIndexed { i, blueprint -> (i + 1) * scoreBlueprint(blueprint, 24, defaultState) }
            .sum()
        println(score)
    }

    fun part2() {
        val defaultState = RobotState(1, 0, 0, 0, 0, 0)

        // test
        // println(scoreBlueprint(blueprints[0], 32, defaultState))
        // println(scoreBlueprint(blueprints[1], 32, defaultState))

        // real
        val score = blueprints
            .take(3)
            .map { scoreBlueprint(it, 32, defaultState) }
            .reduce(Int::times)
        println(score)
    }
}