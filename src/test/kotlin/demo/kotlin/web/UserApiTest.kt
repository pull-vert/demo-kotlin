package demo.kotlin.web

import demo.kotlin.USER_FRED_UUID
import demo.kotlin.security.JWTUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import demo.kotlin.model.Role.ROLE_ADMIN
import org.assertj.core.api.Assertions
import org.springframework.test.web.reactive.server.expectBody

internal class UserApiTest(
        @LocalServerPort private val port: Int,
        @Autowired private val jwtUtil: JWTUtil)
    : ApiTest(port, jwtUtil) {

    @Test
    fun `Verify delete with authenticated USER role fails`() {
        client.delete().uri("/api/users/{userId}", USER_FRED_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isForbidden
    }

    @Test
    fun `Verify delete with authenticated ADMIN role works`() {
        client.delete().uri("/api/users/{userId}", USER_FRED_UUID)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun `Verify delete with unproperly built uuid provided as uri var fails`() {
        val badUuid = "bad_uuid"
        client.delete().uri("/api/users/{userId}", badUuid)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith {
                    val error = it.responseBody!!
                    Assertions.assertThat(error["message"]).isEqualTo("Invalid UUID string: $badUuid")
                    Assertions.assertThat(error["path"]).isEqualTo("/api/users/$badUuid")
                    Assertions.assertThat(error["timestamp"]).isNotNull
                    Assertions.assertThat(error["status"]).isEqualTo(400)
                    Assertions.assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    // todo : test what happens when {userId} is a correct UUID but user doesn't exist + test for restDocs (+ adoc)
}
