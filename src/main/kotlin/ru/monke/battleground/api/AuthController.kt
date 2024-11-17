package ru.monke.battleground.api

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.monke.battleground.domain.auth.SignUpUseCase

fun Route.authController() {
    val signUpUseCase by inject<SignUpUseCase>()

    route("/auth") {
        route("/ping") {
            get {
                call.respondText("pong")
            }
        }

        route("sign_up") {
            post {
                val request = call.receive<SignUpRequest>()

                println(request.email)
                println(request.password)
                println(request.nickname)
            }
        }
    }
}