package example.com

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import ru.monke.battleground.domain.matchmaking.model.TeamSize
import ru.monke.battleground.module
import ru.monke.battleground.view.ConnectView
import ru.monke.battleground.view.SignUpView
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatchTests {

    @Test
    fun `test user signs up and creates team`() = testApplication {
        application {
            module()
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }
        val token = Json.decodeFromString<SignUpView>(signUpResponse.bodyAsText()).token

        val createTeamRequest = client.post("match/start") {
            contentType(ContentType.Application.Json)
            setBody("""{"teamSize": "${TeamSize.Solo}"}""")
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, createTeamRequest.status)
        assertTrue(createTeamRequest.bodyAsText().contains("teamCode"))
    }

    @Test
    fun `test two user signs up and second connects to team`() = testApplication {
        application {
            module()
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse1 = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }
        val token1 = Json.decodeFromString<SignUpView>(signUpResponse1.bodyAsText()).token

        val createBody = client.post("match/start") {
            contentType(ContentType.Application.Json)
            setBody("""{"teamSize": "${TeamSize.Duo}"}""")
            header("Authorization", "Bearer $token1")
        }.bodyAsText()
        val teamCode = Json.decodeFromString<ConnectView>(createBody).teamCode


        val signUpResponse2 = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "seoncdEmail", "password": "$password", "nickname": "Beijin"}""")
        }
        val token2 = Json.decodeFromString<SignUpView>(signUpResponse2.bodyAsText()).token

        val joinTeamRequest = client.post("match/connect/$teamCode") {
            header("Authorization", "Bearer $token2")
        }

        assertEquals(HttpStatusCode.OK, joinTeamRequest.status)
    }

    @Test
    fun `test user signs up, creates team and click ready`() = testApplication {
        application {
            module()
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
        }

        assertEquals(HttpStatusCode.OK, readyRequest.status)
        assertTrue(readyRequest.bodyAsText().contains("sessionId"))
    }

    @Test
    fun `tests team doesnt exists`() = testApplication {
        application {
            module()
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }
        val token = Json.decodeFromString<SignUpView>(signUpResponse.bodyAsText()).token

        val joinTeamRequest = client.post("match/connect/hdhdh") {
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.InternalServerError, joinTeamRequest.status)
    }


}