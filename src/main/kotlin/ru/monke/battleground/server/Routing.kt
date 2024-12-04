package ru.monke.battleground.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.monke.battleground.api.auth.authController
import ru.monke.battleground.api.game.gameController
import ru.monke.battleground.api.match.matchmakingController
import ru.monke.battleground.api.stats.statisticsController

fun Application.configureRouting() {
    routing {
        mockData()
        authController()
        matchmakingController()
        gameController()
        statisticsController()
    }

}
