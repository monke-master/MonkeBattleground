package ru.monke.battleground.api.game

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.auth.usecase.ValidateAccountUseCase
import ru.monke.battleground.domain.game.GameInteractor
import ru.monke.battleground.server.getAccountId
import ru.monke.battleground.view.GameView

private const val GAME_ID = "game_id"
private const val PLAYER_ID = "player_id"

fun Route.gameController() {
    val gameInteractor: GameInteractor = KoinPlatform.getKoin().get()
    val validateAccountUseCase: ValidateAccountUseCase = KoinPlatform.getKoin().get()

    route("/game") {
        authenticate {
            webSocket("/ongoing_game/{$GAME_ID}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized, null)
                val accountId = principal.payload.getAccountId()
                validateAccountUseCase.execute(accountId).getOrNull()
                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized, null)

                val gameId = call.parameters[GAME_ID] ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Game ID is missing")
                )

                val gameFlow = gameInteractor.getGame(gameId) ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Game not found")
                )

                val job = launch {
                    gameFlow.collect { game ->
                        val gameView = GameView(game)
                        send(Frame.Text(Json.encodeToString(gameView)))
                    }
                }

                try {
                    incoming.consumeEach {}
                } catch (e: Exception) {
                    println("WebSocket error: ${e.message}")
                } finally {
                    job.cancel()
                }
            }

//            webSocket("/$PLAYER_ID") {
//                val principal = call.principal<JWTPrincipal>()
//                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized, null)
//                val accountId = principal.payload.getAccountId()
//                validateAccountUseCase.execute(accountId).getOrNull()
//                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized, null)
//            }
        }
    }
}