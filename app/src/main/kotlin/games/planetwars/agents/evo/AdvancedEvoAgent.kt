package games.planetwars.agents.evo

import games.planetwars.agents.Action
import games.planetwars.agents.DoNothingAgent
import games.planetwars.agents.PlanetWarsAgent
import games.planetwars.agents.PlanetWarsPlayer
import games.planetwars.core.*
import kotlin.random.Random
import kotlin.math.max
import kotlin.math.min

/**
 * A more advanced and efficient Evolutionary Agent for Planet Wars using:
 * - Population-based evolution (μ + λ)
 * - Tournament selection
 * - Elitism
 * - Adaptive mutation
 * - Rollout-based fitness with early termination for poor solutions
 */
data class AdvancedEvoAgent(
    var populationSize: Int = 20,
    var offspringSize: Int = 40,
    var tournamentSize: Int = 3,
    var elitism: Int = 2,
    var sequenceLength: Int = 200,
    var generations: Int = 10,
    var baseMutationProb: Double = 0.3,
    var adaptiveMutation: Boolean = true,
    var epsilon: Double = 1e-6,
    var timeLimitMillis: Long = 30,
    var opponentModel: PlanetWarsAgent = DoNothingAgent(),
) : PlanetWarsPlayer() {

    internal var random = Random
    private var buffer: List<ScoredSolution>? = null

    data class ScoredSolution(val score: Double, val solution: FloatArray)

    override fun getAgentType(): String {
        return "AdvancedEvoAgent-$populationSize-$offspringSize-$generations"
    }

    override fun getAction(gameState: GameState): Action {
        val initialPopulation = buffer ?: List(populationSize) { ScoredSolution(0.0, randomPoint()) }
        val wrapper = GameStateWrapper(gameState, params, player, opponentModel)
        val evaluatedPop = initialPopulation.map { ind ->
            val fit = evalSeq(wrapper, ind.solution)
            ScoredSolution(fit, ind.solution)
        }.sortedByDescending { it.score }

        var population = evaluatedPop.take(populationSize)

        var bestSoFar = population.first()
        val startTime = System.currentTimeMillis()

        for (gen in 0 until generations) {
            if (System.currentTimeMillis() - startTime > timeLimitMillis) break

            val nextGen = mutableListOf<ScoredSolution>()
            // Elitism: keep top N
            nextGen.addAll(population.take(elitism))

            // Breed offspring
            while (nextGen.size < offspringSize) {
                val parent = tournamentSelect(population)
                val mutProb = if (adaptiveMutation) {
                    // Lower mutation rate for better individuals
                    baseMutationProb * (1.0 - (parent.score - population.last().score + epsilon) /
                            (population.first().score - population.last().score + epsilon))
                } else baseMutationProb

                val child = mutate(parent.solution, mutProb)
                val fit = evalSeq(wrapper, child)
                nextGen.add(ScoredSolution(fit, child))
            }

            // Select survivors for next generation
            population = (population + nextGen)
                .sortedByDescending { it.score }
                .take(populationSize)
            if (population.first().score > bestSoFar.score) {
                bestSoFar = population.first()
            }
        }
        buffer = listOf(bestSoFar)
        // Use the best individual's action
        val act = wrapper.getAction(gameState, bestSoFar.solution[0], bestSoFar.solution[1])
        return act
    }

    private fun tournamentSelect(pop: List<ScoredSolution>): ScoredSolution {
        val indices = List(tournamentSize) { random.nextInt(pop.size) }
        return indices.map { pop[it] }.maxByOrNull { it.score }!!
    }

    private fun mutate(v: FloatArray, mutProb: Double): FloatArray {
        val n = v.size
        val x = FloatArray(n)
        for (i in 0 until n) {
            x[i] = if (random.nextDouble() < mutProb) random.nextFloat() else v[i]
        }
        return x
    }

    private fun randomPoint(): FloatArray {
        val p = FloatArray(sequenceLength)
        for (i in p.indices) {
            p[i] = random.nextFloat()
        }
        return p
    }

    private fun evalSeq(wrapper: GameStateWrapper, seq: FloatArray): Double {
        // Early termination if the solution is obviously poor
        var ix = 0
        var score = 0.0
        wrapper.forwardModel = ForwardModel(wrapper.gameState.deepCopy(), wrapper.params)
        while (ix < seq.size && !wrapper.forwardModel.isTerminal()) {
            val from = seq[ix]
            val to = seq[ix + 1]
            val myAction = wrapper.getAction(wrapper.gameState, from, to)
            val opponentAction = wrapper.opponentModel.getAction(wrapper.gameState)
            val actions = mapOf(wrapper.player to myAction, wrapper.player.opponent() to opponentAction)
            wrapper.forwardModel.step(actions)
            // Early exit: If hopeless (e.g. lost all ships)
            val ships = wrapper.forwardModel.getShips(wrapper.player)
            if (ships <= 0.0) {
                return -1e9
            }
            ix += GameStateWrapper.shiftBy
        }
        score = wrapper.scoreDifference()
        return score
    }
}

fun main() {
    val gameParams = GameParams(numPlanets = 10)
    val gameState = GameStateFactory(gameParams).createGame()
    val agent = AdvancedEvoAgent()
    agent.prepareToPlayAs(Player.Player1, gameParams)
    println(agent.getAgentType())
    val action = agent.getAction(gameState)
    println(action)
}