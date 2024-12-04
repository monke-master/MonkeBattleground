package example.com

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.junit.Test
import ru.monke.battleground.domain.matchmaking.model.TeamSize
import ru.monke.battleground.module
import ru.monke.battleground.view.*
import kotlin.test.assertTrue

class GameTests {

    @Test
    fun `test game connection`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(WebSockets)
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }
        val token = Json.decodeFromString<SignUpView>(signUpResponse.bodyAsText()).token

        val createBody = client.post("match/start") {
            contentType(ContentType.Application.Json)
            setBody("""{"teamSize": "${TeamSize.Solo}"}""")
            header("Authorization", "Bearer $token")
        }.bodyAsText()
        val teamCode = Json.decodeFromString<ConnectView>(createBody).teamCode

        val readyRequest = client.post("match/ready/$teamCode") {
            header("Authorization", "Bearer $token")
        }.bodyAsText()
        val sessionId = Json.decodeFromString<TeamReadyView>(readyRequest).sessionId

        val session = client.webSocket("/match/session/$sessionId", request = {
            headers {
                append("Authorization", "Bearer $token")
            }
        }) {
            var started = false
            while (!started) {
                val response = (incoming.receive() as Frame.Text).readText()
                val status = Json.decodeFromString<SessionView>(response).sessionStatus


                if (status is SessionStatusView.Started) {
                    started = true
                    client.webSocket("game/${status.gameId}/ongoing_game", request = {
                        headers {
                            append("Authorization", "Bearer $token")
                        }
                    }) {
                        val game = (incoming.receive() as Frame.Text).readText()

                        assertTrue(game.contains("id"))
                        assertTrue(game.contains("mapView"))
                    }
                }
            }

        }
    }
}