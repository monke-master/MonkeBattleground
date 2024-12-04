package ru.monke.battleground.api.stats

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.stats.GetStatisticsUseCase

private const val ACCOUNT_ID = "account_id"

fun Route.statisticsController() {

    val getStatsUseCase: GetStatisticsUseCase = KoinPlatform.getKoin().get()

    authenticate {
        route("/statistics") {
            route("/{$ACCOUNT_ID}") {
                get {
                    val accountId = call.parameters[ACCOUNT_ID]
                        ?: return@get call.respond(HttpStatusCode.BadRequest, null)

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