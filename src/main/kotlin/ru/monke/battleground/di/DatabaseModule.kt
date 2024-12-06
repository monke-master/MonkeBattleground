package ru.monke.battleground.di

import io.ktor.server.application.*
import org.koin.dsl.module
import java.sql.Connection
import java.sql.DriverManager

fun Application.databaseModule() = module {
    single<Connection> { connectToPostgres(true)  }
}


private fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        val url = "jdbc:postgresql://db:5432/gamedb"
        val user = "admin"
        val password = "admin"

        return DriverManager.getConnection(url, user, password)
    }
}
