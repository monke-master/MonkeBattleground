package example.com

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import ru.monke.battleground.module
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthTests {

    @Test
    fun testAuthEndpoint() = testApplication {
        application {
            module()
        }

        client.get("/auth/ping").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("pong", bodyAsText())
        }
    }

    @Test
    fun testSignUpEndpoint() = testApplication {
        application {
            module()
        }

        val response = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "goidyakjfjfj",  "password": "goida", "nickname": "Beijin"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("token"))
    }

    @Test
    fun testSignUpAndSignInEndpoint() = testApplication {
        application {
            module()
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }

        val signInResponse = client.post("/auth/sign_in") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password"}""")
        }

        assertEquals(HttpStatusCode.OK, signUpResponse.status)
        assertEquals(HttpStatusCode.OK, signInResponse.status)
        assertTrue(signInResponse.bodyAsText().contains("token"))
    }

    @Test
    fun testUserNotFound() = testApplication {
        application {
            module()
        }

        val email = "igor22@mail.com"
        val password = "Berkoff"

        val signInResponse = client.post("/auth/sign_in") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password"}""")
        }

        assertEquals(HttpStatusCode.NotFound, signInResponse.status)
    }

    @Test
    fun testWrongPassword() = testApplication {
        application {
            module()
        }

        val email = "igor@mail.com"
        val password = "Berkoff"

        val signUpResponse = client.post("/auth/sign_up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "$password", "nickname": "Beijin"}""")
        }

        val signInResponse = client.post("/auth/sign_in") {
            contentType(ContentType.Application.Json)
            setBody("""{"email": "$email", "password": "wrong"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, signInResponse.status)
    }

}