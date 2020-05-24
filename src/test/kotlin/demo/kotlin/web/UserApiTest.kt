package demo.kotlin.web

import demo.kotlin.USER_BOSS_UUID
import demo.kotlin.USER_FRED_UUID
import demo.kotlin.USER_TO_DELETE_UUID
import demo.kotlin.entities.Role.ROLE_ADMIN
import demo.kotlin.web.dtos.UserGetDto
import demo.kotlin.web.dtos.UserSaveDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import java.util.*

internal class UserApiTest : ApiTest() {

    @Test
    fun `Verify delete User with authenticated ADMIN role works`() {
        client.delete().uri("/api/users/{userId}", USER_TO_DELETE_UUID)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `Verify delete User with authenticated USER role fails`() {
        client.delete().uri("/api/users/{userId}", USER_BOSS_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isForbidden
    }

    @Test
    fun `Verify delete User with invalid uuid uri param fails`() {
        val invalidUuid = "invalid_uuid"
        client.delete().uri("/api/users/{userId}", invalidUuid)
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"]).isEqualTo("Invalid UUID string: $invalidUuid")
                    assertThat(error["path"]).isEqualTo("/api/users/$invalidUuid")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `Verify delete User with valid uuid but no user matching fails`() {
       client.delete().uri("/api/users/{userId}", UUID.randomUUID())
                .addAuthHeader(ROLE_ADMIN)
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `Verify findById returns expected User`() {
        client.get().uri("/api/users/{id}", USER_BOSS_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody<UserGetDto>()
                .consumeWith { exchangeResult ->
                    val user = exchangeResult.responseBody!!
                    assertThat(user.username).isEqualTo("Boss")
                    assertThat(user.id).isEqualTo(USER_BOSS_UUID)
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
    fun `Verify save User ok`() {
        client.post().uri("/api/users/")
                .bodyValue(UserSaveDto("William", "password_again"))
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("location") { uri ->
                    assertThat(uri).startsWith("/api/users/")
                    // Then call the returned uri and verify the it returns saved User resource
                    client.get().uri(uri)
                            .addAuthHeader()
                            .exchange()
                            .expectStatus().isOk
                            .expectBody<UserGetDto>()
                            .consumeWith { exchangeResult ->
                                val user = exchangeResult.responseBody!!
                                assertThat(user.username).isEqualTo("William")
                                assertThat(user.id).isNotEmpty()
                                assertThat(user.enabled).isFalse()
                            }
                }
    }

    @Test
    fun `Verify save User with password too short bean validation fails`() {
        client.post().uri("/api/users/")
                .bodyValue(UserSaveDto("Wrong", "pass"))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).contains("password(pass)", "8", "200")
                    assertThat(error["path"]).isEqualTo("/api/users/")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `Verify save User with no password bean validation fails`() {
        val resource = ClassPathResource("web/dtos/user_no_password.json")
        client.post().uri("/api/users/")
                .body(BodyInserters.fromResource(resource))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).contains("password(null)")
                    assertThat(error["path"]).isEqualTo("/api/users/")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `User findById doc`() {
        client.get().uri("/api/users/{id}", USER_BOSS_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findByIdUser",
                        pathParameters(parameterWithName("id").description("ID of the User to search for")),
                        responseFields(
                                fieldWithPath("username").description("username"),
                                fieldWithPath("authorities.[]").description("An array of authorities (roles)"),
                                fieldWithPath("enabled").description("if user is active or disabled"),
//                                fieldWithPath("credentialsNonExpired").description("if user's credential is active or expired"),
//                                fieldWithPath("accountNonExpired").description("if user's account is active or expired"),
//                                fieldWithPath("accountNonLocked").description("if user's account is not locked"),
                                fieldWithPath("id").description("ID of the User document")
                        )))
    }

    @Test
    fun `User Save doc`() {
        val fields = ConstrainedFields(UserSaveDto::class.java)

        client.post().uri("/api/users/")
                .bodyValue(UserSaveDto("User", "password_again_again"))
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .consumeWith(document("saveUser",
                        requestFields(
                                fields.withPath("username").description("username"),
                                fields.withPath("password").description("raw (non encrypted) password")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("GET URI for accessing created User by its ID")
                        )))
    }
}
