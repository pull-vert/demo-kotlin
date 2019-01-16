package demo.kotlin.web

import demo.kotlin.USER_FRED_UUID
import demo.kotlin.security.JWTUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import demo.kotlin.model.Role.ROLE_ADMIN

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

    // todo : test what happens when {userId} doesn't exist + test for restDocs (+ adoc)
}
