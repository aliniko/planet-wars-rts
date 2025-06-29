package games.planetwars.view

import games.planetwars.agents.random.BetterRandomAgent
import games.planetwars.agents.random.CarefulRandomAgent
import games.planetwars.agents.random.SmarterAgent
import games.planetwars.agents.evo.AdvancedEvoAgent
import games.planetwars.agents.ForwardThinker.RHEAAgent_v1_3

import games.planetwars.agents.random.PureRandomAgent
import games.planetwars.core.GameParams
import games.planetwars.runners.GameRunner
import games.planetwars.core.GameStateFactory
import xkg.jvm.AppLauncher

fun main() {
    val gameParams = GameParams(numPlanets = 20, maxTicks = 1000)
    val gameState = GameStateFactory(gameParams).createGame()
//   val agent2 = BetterRandomAgent()
//    val agent1 = PureRandomAgent()
    val agent1 = SmarterAgent()
//    val agent2 = CarefulRandomAgent()

    val agent2 = RHEAAgent_v1_3()
//    val agent1 = games.planetwars.agents.DoNothingAgent()
//    val agent1 = games.planetwars.agents.BetterRandomAgent()
    val gameRunner = GameRunner(agent1, agent2, gameParams)

    val title = "${agent1.getAgentType()} : Planet Wars : ${agent2.getAgentType()}"
    AppLauncher(
        preferredWidth = gameParams.width,
        preferredHeight = gameParams.height,
        app = GameView(params = gameParams, gameState = gameState, gameRunner = gameRunner),
        title = title,
        frameRate = 100.0,
    ).launch()
}
