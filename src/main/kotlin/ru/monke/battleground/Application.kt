package ru.monke.battleground

import io.ktor.server.application.*
import ru.monke.battleground.server.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installSwagger()
    configureSerialization()
    configureWebSockets()
    configureKoin()
    configureJWT()
    configureRouting()
}
