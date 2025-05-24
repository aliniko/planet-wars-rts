package games.planetwars.runners

import games.planetwars.agents.PlanetWarsAgent
import games.planetwars.agents.random.BetterRandomAgent
import games.planetwars.agents.random.PureRandomAgent
import games.planetwars.core.*

import games.planetwars.core.GameParams
import games.planetwars.agents.random.SmarterAgent

/*
data class GameRunner(
    val agent1: PlanetWarsAgent,
    val agent2: PlanetWarsAgent,
    val gameParams: GameParams,
) {
    var gameState: GameState = GameStateFactory(gameParams).createGame()
    var forwardModel: ForwardModel = ForwardModel(gameState.deepCopy(), gameParams)
    // call newGame() to reset the game state and agents in the constructor
    init {
        newGame()
    }

    fun runGame() : ForwardModel {
        // runs with a fresh copy of the game state each time
//        val forwardModel = ForwardModel(gameState.deepCopy(), gameParams)

        newGame()
        while (!forwardModel.isTerminal()) {
            val actions = mapOf(
                Player.Player1 to agent1.getAction(forwardModel.state.deepCopy()),
                Player.Player2 to agent2.getAction(forwardModel.state.deepCopy()),
            )
            forwardModel.step(actions)
        }
        return forwardModel
    }

    fun newGame() {
        if (gameParams.newMapEachRun) {
            gameState = GameStateFactory(gameParams).createGame()
        }
        forwardModel = ForwardModel(gameState.deepCopy(), gameParams)
        agent1.prepareToPlayAs(Player.Player1, gameParams)
        agent2.prepareToPlayAs(Player.Player2, gameParams)
    }

    fun stepGame() : ForwardModel {
        if (forwardModel.isTerminal()) {
            return forwardModel
        }
        val actions = mapOf(
            Player.Player1 to agent1.getAction(forwardModel.state),
            Player.Player2 to agent2.getAction(forwardModel.state),
        )
        forwardModel.step(actions)
        return forwardModel
    }

    fun runGames(nGames: Int) : Map<Player, Int> {
        val scores = mutableMapOf(Player.Player1 to 0, Player.Player2 to 0, Player.Neutral to 0)
        for (i in 0 until nGames) {
            val finalModel = runGame()
            val winner = finalModel.getLeader()
            scores[winner] = scores[winner]!! + 1
        }
//        println(forwardModel.statusString())

        return scores
    }
}

fun main() {
    val gameParams = GameParams(numPlanets = 20)
//    val gameState = GameStateFactory(gameParams).createGame()
    val agent1 = PureRandomAgent()
    val agent2 = BetterRandomAgent()
    val gameRunner = GameRunner(agent1, agent2, gameParams)
    val finalModel = gameRunner.runGame()
    println("Game over!")
    println(finalModel.statusString())
    // time to run a bunch of games
    val nGames = 1000
    val t = System.currentTimeMillis()
    val results = gameRunner.runGames(nGames)
    val dt = System.currentTimeMillis() - t
    println(results)
    println("Time per game: ${dt.toDouble() / nGames} ms")
    // also print time per step
    val nSteps = ForwardModel.nUpdates
    println("Time per step: ${dt.toDouble() / nSteps} ms")

    println("Successful actions: ${ForwardModel.nActions}")
    println("Failed actions: ${ForwardModel.nFailedActions}")

}

*/



// my crafted Gammrunner Class

data class GameRunner(
    val agent1: PlanetWarsAgent,
    val agent2: PlanetWarsAgent,
//    val agent3: PlanetWarsAgent,
    val gameParams: GameParams,
) {
    var gameState: GameState = GameStateFactory(gameParams).createGame()
    var forwardModel: ForwardModel = ForwardModel(gameState.deepCopy(), gameParams)
    // call newGame() to reset the game state and agents in the constructor
    init {
        newGame()
    }

    fun runGame() : ForwardModel {
        // runs with a fresh copy of the game state each time
//        val forwardModel = ForwardModel(gameState.deepCopy(), gameParams)

        newGame()
        while (!forwardModel.isTerminal()) {
            val actions = mapOf(
                Player.Player1 to agent1.getAction(forwardModel.state.deepCopy()),
                Player.Player2 to agent2.getAction(forwardModel.state.deepCopy()),
                //Player.Player3 to agent3.getAction(forwardModel.state.deepCopy())
            )
            forwardModel.step(actions)
        }
        return forwardModel
    }

    fun newGame() {
        if (gameParams.newMapEachRun) {
            gameState = GameStateFactory(gameParams).createGame()
        }
        forwardModel = ForwardModel(gameState.deepCopy(), gameParams)
        agent1.prepareToPlayAs(Player.Player1, gameParams)
        agent2.prepareToPlayAs(Player.Player2, gameParams)

       // agent3.prepareToPlayAs(Player.Player3, gameParams)
    }

    fun stepGame() : ForwardModel {
        if (forwardModel.isTerminal()) {
            return forwardModel
        }
        val actions = mapOf(
            Player.Player1 to agent1.getAction(forwardModel.state),
            Player.Player2 to agent2.getAction(forwardModel.state),
        )
        forwardModel.step(actions)
        return forwardModel
    }

    fun runGames(nGames: Int) : Map<Player, Int> {
        val scores = mutableMapOf(Player.Player1 to 0, Player.Player2 to 0, Player.Neutral to 0)
        for (i in 0 until nGames) {
            val finalModel = runGame()
            val winner = finalModel.getLeader()
            scores[winner] = scores[winner]!! + 1
        }
//        println(forwardModel.statusString())

        return scores
    }
}

private fun PlanetWarsAgent.prepareToPlayAs(
    player1: Player,
    gameParams: GameParams
) {
}

fun main() {
    // Instantiate your custom SmarterAgent
    val smarterAgent = SmarterAgent()

    // Instantiate another agent to compete against
    val opponentAgent = BetterRandomAgent()

    // Set game parameters (adjust as needed)
    val gameParams = GameParams(numPlanets = 20, maxTicks = 1000)

    // Create the game runner with your agents
    val gameRunner = GameRunner(smarterAgent, opponentAgent, gameParams)

    // Run the game
    val finalModel = gameRunner.runGame()

    // Print game results
    println("Game over! Winner: ${finalModel.getLeader()}")
    println(finalModel.statusString())

    // Optionally, run multiple games for more evaluation
    val nGames = 10
    val startTime = System.currentTimeMillis()
    val results = gameRunner.runGames(nGames)
    val elapsedMs = System.currentTimeMillis() - startTime
    println("Results after $nGames games: $results")
    println("Average time per game: ${elapsedMs.toDouble() / nGames} ms")
}