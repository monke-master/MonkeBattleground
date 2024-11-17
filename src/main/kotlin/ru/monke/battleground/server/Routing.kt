package ru.monke.battleground.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.monke.battleground.api.authController

fun Application.configureRouting() {
    routing {
        authController()
    }

}
