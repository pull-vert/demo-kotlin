package demo.kotlin.web

import demo.kotlin.model.Cow
import demo.kotlin.security.JWTUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

internal class CowApiTest(
        @LocalServerPort private val port: Int,
        @Autowired private val jwtUtil: JWTUtil)
    : ApiTest(port, jwtUtil) {

    @Test
    fun `Verify findByName returns Margerite`() {
        client.get().uri("/api/cows/Marguerite")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody<Cow>()
                .consumeWith {
                    val cow = it.responseBody!!
                    assertThat(cow.name).isEqualTo("Marguerite")
                    assertThat(cow.lastCalvingDate).isNotNull()
                }
    }

    @Test
    fun `Verify findAll returns 2 Cows`() {
        client.get().uri("/api/cows/")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBodyList<Cow>()
                .hasSize(2)
    }

    @Test
    fun `Verify findByName doc`() {
        client.get().uri("/api/cows/{name}", "Marguerite")
                .addAuthHeader()
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findByNameCow", responseFields(*cowFields())))
    }

    @Test
    fun `Verify findAll doc`() {
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

    /**
     * Cow fields used in requests and responses.
     *
     * @return
     */
    private fun cowFields() = arrayOf(
            fieldWithPath("name").description("Name of the Cow"),
            fieldWithPath("lastCalvingDate").description("Last calving date of the Cow").optional(),
            fieldWithPath("id").description("ID of the Cow document")
    )
}
