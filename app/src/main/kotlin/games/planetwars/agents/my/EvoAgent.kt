package games.planetwars.agents.my

import kotlinx.coroutines.*
import games.planetwars.agents.*
import games.planetwars.core.*



class EvoAgent : PlanetWarsPlayer() {

    override fun getAction(gameState: GameState): Action {
        val nSimulations = 100
        var bestAction: Action = Action.doNothing()
        var bestScore = Double.NEGATIVE_INFINITY

        val legalActions = gameState.getLegalActionsForPlayer(player)
        if (legalActions.isEmpty()) {
            return Action.doNothing()
        }

        for (action in legalActions) {
            val simState = gameState.deepCopy()
            val forward = ForwardModel(simState, params)

            val actionsMap = mutableMapOf<Player, Action>(
                Player.Player1 to action,
                Player.Player2 to action
            )
            forward.step(actionsMap)

            val simulationResults = runBlocking {
                (1..nSimulations).map {
                    async {
                        val sim = forward.state.deepCopy() // Make sure forward.state is accessible
                        simulateRandomPlayout(sim, 10, player)
                    }
                }.awaitAll()
            }

            val scoreSum = simulationResults.sum()
            val avgScore = scoreSum / nSimulations

            if (avgScore > bestScore) {
                bestScore = avgScore
                bestAction = action
            }
        }
        return bestAction
    }

    private fun simulateRandomPlayout(state: GameState, depth: Int, player: Player): Double {
        var simState = state.deepCopy() // Create a fresh copy to avoid mutating original

        for (i in 1..depth) {
            val fm = ForwardModel(simState, params)
            if (fm.isTerminal()) break

            val actions = mutableMapOf<Player, Action>(
                player to (simState.getLegalActionsForPlayer(player).randomOrNull() ?: Action.doNothing()),
                player.opponent() to (simState.getLegalActionsForPlayer(player.opponent()).randomOrNull() ?: Action.doNothing())
            )
            fm.step(actions)
            simState = fm.state // Update simState to the new state after step
        }
       // val scores = mutableMapOf<Player, Double>()


        val scores = mutableMapOf<Player, Double>()
        scores[Player.Player1] = 10.0
        return scores[player] ?: 0.0

    }

    // Use Kotlin's built-in null-safe random function
    private fun List<Action>.randomOrNull(): Action? = this.randomOrNull()

    override fun getAgentType(): String = "EvoAgent"

    override fun prepareToPlayAs(player: Player, params: GameParams, opponent: String?): String {
        this.player = player
        this.params = params
        return getAgentType()
    }

    override fun mapOf(pairs: Pair<Player, Any>, pairs2: Pair<Player, Any>) {
        // Your implementation if needed
    }

    override fun mutableMapOf(
        pairs: Pair<Player, Char>,
        pairs2: Pair<Player, Char>
    ) {
        TODO("Not yet implemented")
    }
}

private fun Unit.asIterable() {
    TODO("Not yet implemented")
}

private fun Unit.randomOrNull(): Action {
    return TODO("Provide the return value")
}

private fun Unit.isEmpty(): Boolean {
    return TODO("Provide the return value")
}