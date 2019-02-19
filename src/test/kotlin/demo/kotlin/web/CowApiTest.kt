package demo.kotlin.web

import demo.kotlin.COW_MARGUERITE_UUID
import demo.kotlin.web.dtos.CowGetDto
import demo.kotlin.web.dtos.CowSaveDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.time.LocalDateTime

internal class CowApiTest : ApiTest() {

    @Test
    fun `Verify findByName returns expected cow`() {
        client.get().uri("/api/cows/name/{name}", "Marguerite")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody<CowGetDto>()
                .consumeWith { exchangeResult ->
                    val cow = exchangeResult.responseBody!!
                    assertThat(cow.name).isEqualTo("Marguerite")
                    assertThat(cow.lastCalvingDate).isNotNull()
                }
    }

    @Test
    fun `Verify findByName returns 404 Not Found if Cow corresponding to provided name is not found`() {
        val name = "no-name"
        client.get().uri("/api/cows/name/{name}", name)
                .addAuthHeader()
                .exchange()
                .expectStatus().isNotFound
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).isEqualTo("No Cow found for $name name")
                    assertThat(error["path"]).isEqualTo("/api/cows/name/no-name")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(404)
                    assertThat(error["error"]).isEqualTo("Not Found")
                }
    }

    @Test
    fun `Verify findAll returns 2 Cows`() {
        client.get().uri("/api/cows/")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBodyList<CowGetDto>()
                .hasSize(2)
    }

    @Test
    fun `Verify findById returns expected Cow`() {
        client.get().uri("/api/cows/{id}", COW_MARGUERITE_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody<CowGetDto>()
                .consumeWith { exchangeResult ->
                    val cow = exchangeResult.responseBody!!
                    assertThat(cow.name).isEqualTo("Marguerite")
                    assertThat(cow.lastCalvingDate).isNotNull()
                    assertThat(cow.id).isEqualTo(COW_MARGUERITE_UUID)
                }
    }

    @Test
    fun `Verify findById with no JWT Token fails`() {
        client.get().uri("/api/cows/{id}", COW_MARGUERITE_UUID)
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `Verify findById with invalid uuid uri param fails`() {
        val invalidUuid = "invalid_uuid"
        client.get().uri("/api/cows/{id}", invalidUuid)
                .addAuthHeader()
                .exchange()
                .expectStatus().isBadRequest
    }

    @Test
    fun `Verify save Cow ok`() {
        client.post().uri("/api/cows/")
                .syncBody(CowSaveDto("Paquerette", null))
                .addAuthHeader()
                .exchange()
                .expectStatus().isCreated
                .expectHeader().value("location") { uri ->
                    assertThat(uri).startsWith("/api/cows/")
                    // Then call the returned uri and verify the it returns saved User resource
                    client.get().uri(uri)
                            .addAuthHeader()
                            .exchange()
                            .expectStatus().isOk
                            .expectBody<CowGetDto>()
                            .consumeWith { exchangeResult ->
                                val cow = exchangeResult.responseBody!!
                                assertThat(cow.name).isEqualTo("Paquerette")
                                assertThat(cow.lastCalvingDate).isNull()
                                assertThat(cow.id).isNotEmpty()
                            }
                }
    }

    @Test
    fun `Verify save Cow with name too short bean validation fails`() {
        client.post().uri("/api/cows/")
                .syncBody(CowSaveDto("t", null))
                .addAuthHeader()
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).contains("name(t)", "2", "50")
                    assertThat(error["path"]).isEqualTo("/api/cows/")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `Verify save Cow with no name bean validation fails`() {
        client.post().uri("/api/cows/")
                .syncBody(CowSaveDto(null, null))
                .addAuthHeader()
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ServerResponseError>()
                .consumeWith { exchangeResult ->
                    val error = exchangeResult.responseBody!!
                    assertThat(error["message"] as String).contains("name(null)")
                    assertThat(error["path"]).isEqualTo("/api/cows/")
                    assertThat(error["timestamp"]).isNotNull
                    assertThat(error["status"]).isEqualTo(400)
                    assertThat(error["error"]).isEqualTo("Bad Request")
                }
    }

    @Test
    fun `Cow findByName doc`() {
        client.get().uri("/api/cows/name/{name}", "Marguerite")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findByNameCow",
                        pathParameters(parameterWithName("name").description("Name of the Cow to search for")),
                        responseFields(*cowFields())))
    }

    @Test
    fun `Cow findAll doc`() {
        client.get().uri("/api/cows/")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findAllCows",
                        responseFields(
                                fieldWithPath("[]").description("An array of cows"))
                                .andWithPrefix("[].", *cowFields())))
    }

    @Test
    fun `Cow findById doc`() {
        client.get().uri("/api/cows/{id}", COW_MARGUERITE_UUID)
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findByIdCow",
                        pathParameters(parameterWithName("id").description("ID of the Cow to search for")),
                        responseFields(*cowFields())))
    }

    @Test
    fun `Cow Save doc`() {
        client.post().uri("/api/cows/")
                .syncBody(CowSaveDto("Cow", LocalDateTime.of(2016, 5, 28, 13, 30)))
                .addAuthHeader()
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .consumeWith(document("saveCow",
                        requestFields(
                                fieldWithPath("name").description("Name of the Cow"),
                                fieldWithPath("lastCalvingDate").description("Last calving date of the Cow").optional()
                        ),
                        responseHeaders(
                                headerWithName("Location").description("GET URI for accessing created Cow by its ID")
                        )))
    }

    /**
     * Cow fields used in responses.
     *
     * @return
     */
    private fun cowFields() = arrayOf(
            fieldWithPath("name").description("Name of the Cow"),
            fieldWithPath("lastCalvingDate").description("Last calving date of the Cow").optional(),
            fieldWithPath("id").description("ID of the Cow document")
    )
}
