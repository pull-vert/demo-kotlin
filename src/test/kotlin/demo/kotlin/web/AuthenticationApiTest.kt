package demo.kotlin.web

import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import demo.kotlin.entities.Role
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
                .syncBody(AuthRequestDto("Fred", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody<AuthResponseDto>()
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
                .syncBody(AuthRequestDto("John", "password"))
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Verify auth incorrect password unauthorized`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequestDto("Fred", "incorrect_password"))
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Auth doc`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequestDto("Fred", "password"))
                .exchange()
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
