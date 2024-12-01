package ru.monke.battleground.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import java.util.*

private const val AUDIENCE = "player"
private const val ISSUER = "monke"
private const val EXPIRES_MS = 1000 * 60 * 60 * 24
private const val SECRET = "berkoff_key"
private const val REALM = "Access to 'Game API'"
private const val ACCOUNT_ID = "account_id"

fun Application.configureJWT() {
    install(Authentication) {
        jwt {
            realm = REALM
            verifier(
                JWT
                    .require(Algorithm.HMAC256(SECRET))
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun generateJWT(accountId: String): String {
    val token = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim(ACCOUNT_ID, accountId)
        .withExpiresAt(Date(System.currentTimeMillis() + EXPIRES_MS))
        .sign(Algorithm.HMAC256(SECRET))

    return token
}

fun Payload.getAccountId(): String {
    val string = claims[ACCOUNT_ID].toString()
    return string.substring(1, string.length - 1)
}