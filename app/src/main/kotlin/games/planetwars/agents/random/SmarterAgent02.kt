package games.planetwars.agents.random

import games.planetwars.agents.Action
import games.planetwars.agents.PlanetWarsPlayer
import games.planetwars.core.GameState
import games.planetwars.core.Planet

/*
class SmarterAgent02 : PlanetWarsPlayer() {

    override fun getAction(gameState: GameState): Action {
        val myPlanets = gameState.planets.filter { it.owner == player && it.nShips > 0 }
        if (myPlanets.isEmpty()) {
            // No ships to send
            return Action(player, -1, -1, 0.0)
        }

        // Defensive: Help weakest owned planet if it is threatened
        val threatenedPlanets = myPlanets.filter { planet ->
            val incoming = gameState.fleets.filter { it.target == planet.id && it.owner != player }
            incoming.sumOf { it.nShips } > planet.nShips * 0.5 // Heuristic: under threat
        }
        if (threatenedPlanets.isNotEmpty()) {
            val weakest = threatenedPlanets.minByOrNull { it.nShips }!!
            val strongest = myPlanets.maxByOrNull { it.nShips }!!
            if (strongest.id != weakest.id && strongest.nShips > weakest.nShips * 2) {
                return Action(player, strongest.id, weakest.id, strongest.nShips / 2)
            }
        }

        // Prioritize capturing neutral planets early, then weak enemy planets
        val neutralPlanets = gameState.planets.filter { it.owner == 0 }
        val enemyPlanets = gameState.planets.filter { it.owner != 0 && it.owner != player }

        // Score targets by ease and value
        fun score(planet: Planet): Double {
            val dist = myPlanets.minOf { it.distance(planet) }
            val value = planet.growthRate * 2.0 - planet.nShips
            return value / (dist + 1)
        }

        val target = (neutralPlanets + enemyPlanets)
            .filter { it.nShips > 0 }
            .maxByOrNull { score(it) }

        val source = myPlanets.maxByOrNull {
            // Prefer high ship count and high growth, but not emptying planets
            it.nShips + it.growthRate * 2
        }!!

        // Only attack if source has enough ships to keep itself safe
        val minGarrison = 10 + source.growthRate * 2 // Adjustable parameter
        val shipsAvailable = source.nShips - minGarrison
        if (target != null && shipsAvailable > target.nShips * 1.2 && shipsAvailable > 0) {
            // Send only the ships needed (overpower a bit to win), but do not strip planet
            val shipsToSend = minOf(shipsAvailable, target.nShips * 1.3)
            return Action(player, source.id, target.id, shipsToSend)
        }

        // No good attack: reinforce weakest planet to prepare for next round
        val weakest = myPlanets.minByOrNull { it.nShips }!!
        if (weakest.id != source.id && source.nShips > weakest.nShips * 2) {
            return Action(player, source.id, weakest.id, source.nShips / 3)
        }

        // Nothing better to do
        return Action(player, -1, -1, 0.0)
    }

    override fun getAgentType(): String {
        return "Optimized SmarterAgent02"
    }
}

 */