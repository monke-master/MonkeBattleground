package ru.monke.battleground

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.monke.battleground.di.appModules

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(appModules)
    }
}
