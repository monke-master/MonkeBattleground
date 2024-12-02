package ru.monke.battleground.api.game

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
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
import ru.monke.battleground.domain.game.models.Coordinates
import ru.monke.battleground.server.getAccountId
import ru.monke.battleground.view.GameView

private const val GAME_ID = "game_id"
private const val PLAYER_ID = "player_id"

fun Route.gameController() {
    val gameInteractor: GameInteractor = KoinPlatform.getKoin().get()
    val validateAccountUseCase: ValidateAccountUseCase = KoinPlatform.getKoin().get()

    route("/game/{$GAME_ID}") {
        authenticate {
            webSocket("/ongoing_game") {
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

            route("/{$PLAYER_ID}") {
                route("/pick_item") {
                    post {
//                        val principal = call.principal<JWTPrincipal>()
//                            ?: return@post call.respond(HttpStatusCode.Unauthorized, null)
//                        val accountId = principal.payload.getAccountId()
//                        validateAccountUseCase.execute(accountId).getOrNull()
//                            ?: return@post call.respond(HttpStatusCode.Unauthorized, null)

                        val gameId = call.parameters[GAME_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)
                        val playerId = call.parameters[PLAYER_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)

                        val request = call.receive<PickItemRequest>()
                        gameInteractor.pickItem(
                            playerId = playerId,
                            gameId = gameId,
                            inventoryX = request.inventoryX,
                            inventoryY = request.inventoryY,
                            itemId = request.itemId
                        ).getOrNull() ?: return@post call.respond(HttpStatusCode.NotFound, null)

                        call.respond(HttpStatusCode.OK, null)
                    }

                }

                route("/move") {
                    post {
                        val gameId = call.parameters[GAME_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)
                        val playerId = call.parameters[PLAYER_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)

                        val request = call.receive<MovePlayerRequest>()

                        gameInteractor.move(
                            gameId = gameId,
                            playerId = playerId,
                            coordinates = Coordinates(request.x, request.y, request.z)
                        ).getOrNull() ?: return@post call.respond(HttpStatusCode.NotFound, null)

                        call.respond(HttpStatusCode.OK, null)
                    }
                }

                route("/shoot") {
                    post {
                        val gameId = call.parameters[GAME_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)
                        val playerId = call.parameters[PLAYER_ID]
                            ?: return@post call.respond(HttpStatusCode.BadRequest, null)

                        val request = call.receive<ShootPlayerRequest>()

                        gameInteractor.shoot(
                            gameId = gameId,
                            playerId = playerId,
                            targetId = request.targetPlayerId,
                            weaponId = request.weaponId
                        ).getOrNull() ?: return@post call.respond(HttpStatusCode.NotFound, null)

                        call.respond(HttpStatusCode.OK, null)
                    }
                }


            }
        }
    }
}