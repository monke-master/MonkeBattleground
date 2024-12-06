package ru.monke.battleground.server

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.installSwagger() {
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
}

fun Route.swagger() {
    route("swagger") {
        swaggerUI("/api.json")
    }
    route("api.json") {
        openApiSpec()
    }
}