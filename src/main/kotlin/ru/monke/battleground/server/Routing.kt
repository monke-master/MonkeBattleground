package ru.monke.battleground.server

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ru.monke.battleground.api.auth.authController
import ru.monke.battleground.api.auth.userController
import ru.monke.battleground.api.game.gameController
import ru.monke.battleground.api.match.matchmakingController
import ru.monke.battleground.api.stats.statisticsController

fun Application.configureRouting() {
    routing {
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }
        install(SwaggerUI) {
            info {
                title = "Example API"
                version = "latest"
                description = "Example API for testing and demonstration purposes."
            }
            server {
                url = "http://localhost:8080"
                description = "Development Server"
            }
        }
        mockData()
        authController()
        matchmakingController()
        gameController()
        statisticsController()
        userController()
    }

}
