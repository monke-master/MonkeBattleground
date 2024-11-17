package ru.monke.battleground.server

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.monke.battleground.di.appModules
import ru.monke.battleground.di.databaseModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModules + databaseModule())
    }
}
