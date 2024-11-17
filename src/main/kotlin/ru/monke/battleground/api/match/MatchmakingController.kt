package ru.monke.battleground.api.match

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.matchmaking.MatchmakingInteractor
import ru.monke.battleground.view.ConnectView

fun Route.matchmakingController() {

    val interactor: MatchmakingInteractor = KoinPlatform.getKoin().get()

    route("/match") {
        authenticate {
            route("/start") {
                post {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, null)

                    val string = principal.payload.claims["account_id"].toString()
                    val accountId = string.substring(1, string.length - 1)
                    val teamSize = call.receive<ConnectRequest>().teamSize
                    val code = interactor.connect(accountId, teamSize)


                    call.respond(HttpStatusCode.OK, ConnectView(code))
                }
            }
        }

    }
}