package demo.kotlin.web

import demo.kotlin.entities.Role
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.expectBody

internal class AuthenticationApiTest(@Autowired private val jwtUtil: JWTUtil) : ApiTest() {

    @Test
    fun `Verify auth ok`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequestDto("Fred", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody<AuthResponseDto>()
                .consumeWith { exchangeResult ->
                    val authResponse = exchangeResult.responseBody!!
                    assertThat(authResponse.token)
                            .isNotEmpty()
                            .matches { token -> jwtUtil.validateToken(token) }
                            .satisfies {token ->
                                val claims = jwtUtil.getAllClaimsFromToken(token)
                                val roles = claims.get("authorities", List::class.java)
                                        .map { authority -> Role.valueOf(authority as String) }
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
    fun `Verify auth no password bean validation fails`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequestDto("John", null))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).contains("password(null)")
                    assertThat(error["path"]).isEqualTo("/auth/")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
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
