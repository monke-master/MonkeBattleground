package ru.monke.battleground.api

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.auth.AccountNotFoundException
import ru.monke.battleground.domain.auth.SignInUseCase
import ru.monke.battleground.domain.auth.SignUpUseCase
import ru.monke.battleground.domain.auth.WrongPasswordException
import ru.monke.battleground.server.generateJWT
import ru.monke.battleground.view.SignInView
import ru.monke.battleground.view.SignUpView

fun Route.authController() {
    val signUpUseCase: SignUpUseCase = KoinPlatform.getKoin().get()
    val signInUseCase: SignInUseCase = KoinPlatform.getKoin().get()

    route("/auth") {
        route("/ping") {
            get {
                call.respondText("pong")
            }
        }

        route("sign_up") {
            post {
                val request = call.receive<SignUpRequest>()
                val result = signUpUseCase.execute(
                    email = request.email,
                    password = request.password,
                    nickname = request.nickname
                )

                result.onSuccess {
                    call.respond(HttpStatusCode.OK, SignUpView(generateJWT(it)))
                }.onFailure {
                    call.respond(HttpStatusCode(500, it.toString()))
                }
            }
        }

        route("sign_in") {
            post {
                val request = call.receive<SignInRequest>()
                val result = signInUseCase.execute(request.email, request.password)

                result.onSuccess {  account ->
                    call.respond(HttpStatusCode.OK, SignInView(generateJWT(account.id)))
                }.onFailure { error ->
                    when (error) {
                        is WrongPasswordException -> {
                            call.respond(HttpStatusCode(401, "Password is wrong!"))
                        }
                        is AccountNotFoundException -> {
                            call.respond(HttpStatusCode(404, "Account not found"))
                        }
                        else -> {
                            call.respond(HttpStatusCode(500, error.toString()))
                        }
                    }

                }
            }
        }
    }
}