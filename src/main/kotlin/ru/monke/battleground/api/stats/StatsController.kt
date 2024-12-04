package ru.monke.battleground.api.stats

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.stats.GetStatisticsUseCase
import ru.monke.battleground.server.getAccountId

fun Route.statisticsController() {

    val getStatsUseCase: GetStatisticsUseCase = KoinPlatform.getKoin().get()

    authenticate {
        route("/statistics") {
            route("/for_user") {
                get {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@get call.respond(HttpStatusCode.Unauthorized, null)
                    val accountId = principal.payload.getAccountId()

                    getStatsUseCase.execute(accountId)
                        .onSuccess { stats ->
                            call.respond(HttpStatusCode.OK, stats.map { PlayerStatisticsView(it) })
                        }
                        .onFailure {
                            call.respond(HttpStatusCode.InternalServerError, null)
                        }
                }
            }
        }
    }
}