package demo.kotlin.web

import demo.kotlin.web.dtos.AuthRequest
import demo.kotlin.web.dtos.AuthResponse
import demo.kotlin.model.entities.Role
import demo.kotlin.security.JWTUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.expectBody

internal class AuthenticationApiTest(
        @LocalServerPort private val port: Int,
        @Autowired private val jwtUtil: JWTUtil)
    : ApiTest(port, jwtUtil) {

    @Test
    fun `Verify auth ok`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequest("Fred", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody<AuthResponse>()
                .consumeWith {
                    val authResponse = it.responseBody!!
                    assertThat(authResponse.token)
                            .isNotEmpty()
                            .matches { jwtUtil.validateToken(it) }
                            .satisfies {
                                val claims = jwtUtil.getAllClaimsFromToken(it)
                                val roles = claims.get("authorities", List::class.java)
                                        .map { Role.valueOf(it as String) }
                                assertThat(roles).containsOnly(Role.ROLE_USER)
                            }
                }
    }

    @Test
    fun `Verify auth unknown user unauthorized`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequest("John", "password"))
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Verify auth incorrect password unauthorized`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequest("Fred", "incorrect_password"))
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Verify auth doc`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequest("Fred", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("auth",
                        requestFields(
                                fieldWithPath("username").description("username for authentication"),
                                fieldWithPath("password").description("raw (non encrypted) password for authentication")
                        ),
                        responseFields(
                                fieldWithPath("token").description("Generated JWT authentication token")
                        )))
    }
}
