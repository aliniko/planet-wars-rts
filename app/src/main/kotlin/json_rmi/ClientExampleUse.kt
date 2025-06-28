package json_rmi

import games.planetwars.agents.Action
import games.planetwars.core.GameParams
import games.planetwars.core.GameStateFactory
import games.planetwars.core.Player
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import json_rmi.endAgent
import json_rmi.initAgent
import json_rmi.invokeRemoteMethod
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject


fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    val className = "games.planetwars.agents.random.CarefulRandomAgent"
    client.webSocket("ws://localhost:8080/ws") {
        val objectId = initAgent(className)

        val params = GameParams(numPlanets = 10, initialNeutralRatio = 0.0)

        val prepareResponse = invokeRemoteMethod(
            objectId=objectId,
            method="prepareToPlayAs",
            args = listOf(Player.Player1, params, "DummyOpponent", null),
        )
        println("prepareToPlayAs Response: $prepareResponse")

        val gameState = GameStateFactory(params).createGame()
        val actionResponse = invokeRemoteMethod(
            objectId,
            "getAction",
            args = listOf(gameState),
        )
        println("getAction Response: $actionResponse")

        // also check what type the response is
        val jsonResp = json.parseToJsonElement(actionResponse).jsonObject
        val result = jsonResp["result"]
        if (result != null && result is JsonObject) {
            val action = json.decodeFromJsonElement(Action.serializer(), result)
            println("Decoded Action: $action")
        }

        endAgent(objectId)
    }

    client.close()
}
