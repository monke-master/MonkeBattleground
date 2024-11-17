package ru.monke.battleground

import example.com.*
import io.ktor.server.application.*
import ru.monke.battleground.server.configureFrameworks
import ru.monke.battleground.server.configureJWT
import ru.monke.battleground.server.configureRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureSockets()
    configureFrameworks()
    configureJWT()
    configureRouting()
}
