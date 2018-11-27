package demo.kotlin.web

import demo.kotlin.dto.AuthRequest
import demo.kotlin.dto.AuthResponse
import demo.kotlin.model.Cow
import demo.kotlin.security.JWTUtil
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.netty.http.client.HttpClient


@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs(uriScheme = "https", uriPort = 8443)
class ApiTest(
        @LocalServerPort private val port: Int,
        @Autowired private val jwtUtil: JWTUtil
) {

    lateinit var client: WebTestClient

    @BeforeEach
    fun beforeAll(restDocumentation: RestDocumentationContextProvider) {
        val sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        val httpClient = HttpClient.create().secure{ t -> t.sslContext(sslContext) }
        val httpConnector = ReactorClientHttpConnector(httpClient)

        client = WebTestClient.bindToServer(httpConnector).baseUrl("https://localhost:$port")
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build()
    }

    @Test
    fun `Verify findByName returns Margerite`() {
        client.get().uri("/api/cows/Marguerite")
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
                .exchange()
                .expectStatus().isOk
                .expectBodyList<Cow>()
                .hasSize(2)
    }

    @Test
    fun `Verify findByName doc`() {
        client.get().uri("/api/cows/{name}", "Marguerite")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(document("findByNameCow", responseFields(*cowFields())))
    }

    @Test
    fun `Verify findAll doc`() {
        client.get().uri("/api/cows/")
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

    @Test
    fun `Verify auth ok`() {
        client.post().uri("/auth/")
                .syncBody(AuthRequest("Fred", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody<AuthResponse>()
                .consumeWith {
                    val authResponse = it.responseBody!!
                    assertThat(authResponse.token).isNotEmpty()
                    assertThat(jwtUtil.validateToken(authResponse.token)).isTrue()
                }
    }
}
