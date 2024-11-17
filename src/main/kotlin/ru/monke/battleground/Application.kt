package ru.monke.battleground

import example.com.*
import io.ktor.server.application.*
import ru.monke.battleground.server.configureKoin
import ru.monke.battleground.server.configureJWT
import ru.monke.battleground.server.configureRouting
import ru.monke.battleground.server.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSockets()
    configureKoin()
    configureJWT()
    configureRouting()
}
