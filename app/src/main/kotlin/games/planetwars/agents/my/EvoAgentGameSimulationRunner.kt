import games.planetwars.agents.Action
import games.planetwars.agents.my.EvoAgent
import games.planetwars.core.*

fun main() {
    // Initialize game parameters
    val gameParams = GameParams(
        width = 50,
        height = 50,
        numPlanets = 20
    )

    // Create an initial game state
    val initialGameState = GameStateFactory(gameParams).createGame()

    // Instantiate your EvoAgent
    val agent = EvoAgent()

    // Prepare agent with player info and params
    val agentType = agent.prepareToPlayAs(Player.Player1, gameParams, null)
    println("Selected agent type: $agentType")

    var gameState = initialGameState

    // Run the game for a fixed number of steps
    repeat(50) { step ->
        println("Step $step")
        val action = agent.getAction(gameState)  // Get the action from your agent
        val forwardModel = ForwardModel(gameState.deepCopy(), gameParams)

        // Create actions map for a two-player game scenario
        val actionsMap = mutableMapOf<Player, Action>(
            Player.Player1 to action,
            Player.Player2 to Action.doNothing() // For simplicity, opponent does nothing
        )

        // Step the game
        forwardModel.step(actionsMap)
        gameState = forwardModel.state

        // Optionally, log or visualize game state
        println("Game state after step $step: $gameState")
    }
}