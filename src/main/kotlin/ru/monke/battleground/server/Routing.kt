package ru.monke.battleground.server

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import ru.monke.battleground.api.auth.authController
import ru.monke.battleground.api.auth.userController
import ru.monke.battleground.api.game.gameController
import ru.monke.battleground.api.match.matchmakingController
import ru.monke.battleground.api.stats.statisticsController

fun Application.configureRouting() {
    routing {
        mockData()
        swagger()
        authController()
        matchmakingController()
        gameController()
        statisticsController()
        userController()
    }

}
