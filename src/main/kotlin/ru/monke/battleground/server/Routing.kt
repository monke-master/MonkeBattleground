package ru.monke.battleground.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.monke.battleground.api.auth.authController
import ru.monke.battleground.api.match.matchmakingController

fun Application.configureRouting() {
    routing {
        authController()
        matchmakingController()
    }

}
