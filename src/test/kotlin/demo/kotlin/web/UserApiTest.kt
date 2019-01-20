package demo.kotlin.web

import demo.kotlin.USER_BOSS_UUID
import demo.kotlin.USER_FRED_UUID
import demo.kotlin.model.entities.Role.ROLE_ADMIN
import demo.kotlin.model.entities.User
import demo.kotlin.security.JWTUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

internal class UserApiTest(
        @LocalServerPort private val port: Int,
        @Autowired private val jwtUtil: JWTUtil
) : ApiTest(port, jwtUtil) {

    @Rollback
    @Test
    fun `Verify delete with authenticated ADMIN role works`() {
        client.delete().uri("/api/users/{userId}", USER_FRED_UUID)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `Verify delete with authenticated USER role fails`() {
        client.delete().uri("/api/users/{userId}", USER_BOSS_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isForbidden
    }

    @Test
    fun `Verify delete with invalid uuid uri param fails`() {
        val invalidUuid = "invalid_uuid"
        client.delete().uri("/api/users/{userId}", invalidUuid)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith {
                    val error = it.responseBody!!
                    assertThat(error["message"]).isEqualTo("Invalid UUID string: $invalidUuid")
                    assertThat(error["path"]).isEqualTo("/api/users/$invalidUuid")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `Verify delete with valid uuid but no user matching fails`() {
       client.delete().uri("/api/users/{userId}", UUID.randomUUID())
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `Verify findById returns expected user`() {
        client.get().uri("/api/users/{id}", USER_BOSS_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody<User>()
                .consumeWith {
                    val user = it.responseBody!!
                    assertThat(user.username).isEqualTo("Boss")
                    assertThat(user.id).isEqualTo(UUID.fromString(USER_BOSS_UUID))
                }
    }

    @Test
    fun `Verify findById with no JWT Token fails`() {
        client.get().uri("/api/users/{id}", USER_BOSS_UUID)
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Verify findById with invalid uuid uri param fails`() {
        val invalidUuid = "invalid_uuid"
        client.get().uri("/api/users/{id}", invalidUuid)
                .addAuthHeader()
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun `Verify save ok`() {
        client.post().uri("/api/users/")
                .syncBody(User("William", "password_again"))
                .addAuthHeader()
                .exchange()
                .expectStatus().isCreated
    }

    // todo : test for restDocs (+ adoc)
}
