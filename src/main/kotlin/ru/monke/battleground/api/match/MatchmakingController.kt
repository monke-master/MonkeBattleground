package ru.monke.battleground.api.match

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.matchmaking.MatchmakingInteractor
import ru.monke.battleground.server.getAccountId
import ru.monke.battleground.view.ConnectView
import ru.monke.battleground.view.SessionView
import ru.monke.battleground.view.TeamReadyView

private const val TEAM_CODE = "team_code"
private const val SESSION_ID = "session_id"

fun Route.matchmakingController() {

    val interactor: MatchmakingInteractor = KoinPlatform.getKoin().get()

    route("/match") {
        authenticate {
            route("/start") {
                post {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, null)

                    val accountId = principal.payload.getAccountId()
                    val teamSize = call.receive<ConnectRequest>().teamSize
                    val code = interactor.connect(accountId, teamSize)

                    call.respond(HttpStatusCode.OK, ConnectView(code))
                }
            }

            route("/connect/{$TEAM_CODE}") {
                post {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, null)

                    val accountId = principal.payload.getAccountId()
                    val teamCode = call.parameters[TEAM_CODE] ?: ""

                    interactor.connectToTeam(accountId, teamCode)
                        .onSuccess { call.respond(HttpStatusCode.OK) }
                        .onFailure { call.respond(HttpStatusCode(500, it.toString())) }
                }
            }

            route("/ready/{$TEAM_CODE}") {
                post {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, null)

                    val accountId = principal.payload.getAccountId()
                    val teamCode = call.parameters[TEAM_CODE] ?: ""

                    interactor.setTeamReady(teamCode)
                        .onFailure { call.respond(HttpStatusCode(500, it.toString())) }

                    interactor.findSessionForTeam(teamCode)
                        .onSuccess { call.respond(HttpStatusCode.OK, TeamReadyView(it)) }
                        .onFailure { call.respond(HttpStatusCode(500, it.toString())) }
                }
            }

            webSocket("/session/{$SESSION_ID}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@webSocket call.respond(HttpStatusCode.Unauthorized, null)

                val accountId = principal.payload.getAccountId()
                val sessionId = call.parameters["session_id"] ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Session ID is missing")
                )

                send(Frame.Text("Connected"))

                val session = interactor.getSession(sessionId) ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Session not found")
                )

                val job = launch {
                    session.collect {
                        val view = SessionView(it.id, it.sessionStatus)
                        send(Frame.Text(Json.encodeToString(view)))
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
        }
    }

}