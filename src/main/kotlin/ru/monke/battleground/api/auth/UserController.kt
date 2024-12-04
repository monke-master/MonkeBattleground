package ru.monke.battleground.api.auth

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.auth.usecase.DeleteAccountUseCase
import ru.monke.battleground.server.getAccountId


fun Route.userController() {
    val deleteAccountUseCase: DeleteAccountUseCase = KoinPlatform.getKoin().get()

    authenticate {
        route("/account") {
            delete {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, null)
                val accountId = principal.payload.getAccountId()

                deleteAccountUseCase.execute(accountId)
                    .onSuccess { call.respond(HttpStatusCode.OK, null) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, it.toString()) }
            }
        }
    }
}