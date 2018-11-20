package demo.kotlin.web

import demo.kotlin.model.Cow
import io.netty.handler.ssl.*
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
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
        @LocalServerPort val port: Int
) {

    lateinit var client: WebTestClient

    @BeforeEach
    fun beforeAll(restDocumentation: RestDocumentationContextProvider) {
//        val provider = if (OpenSsl.isAlpnSupported())
//            io.netty.handler.ssl.SslProvider.OPENSSL
//        else
//            io.netty.handler.ssl.SslProvider.JDK
//        // trust any CA, to communicate with self signed server keystore
//        val sslContext = SslContextBuilder.forClient()
//                .sslProvider(provider)
//                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
//                .applicationProtocolConfig(ApplicationProtocolConfig(
//                        ApplicationProtocolConfig.Protocol.ALPN,
//                        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
//                        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
//                        ApplicationProtocolNames.HTTP_2))
//                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
//
//        val tcpClient = TcpClient.create().secure{ t -> t.sslContext(sslContext) }
//        val httpClient = HttpClient.from(tcpClient).protocol(HttpProtocol.H2)

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
    fun `Verify findByName JSON API returns Margerite`() {
        client.get().uri("/api/cows/Marguerite")
                .exchange()
                .expectBody<Cow>()
                .consumeWith {
                    val cow = it.responseBody
                    assertThat(cow?.name).isEqualTo("Marguerite")
                    assertThat(cow?.lastCalvingDate).isNotNull()
                }
    }

    @Test
    fun `Verify findAll JSON API returns 2 Cows`() {
        client.get().uri("/api/cows/")
                .exchange()
                .expectBodyList<Cow>()
                .hasSize(2)
    }

    @Test
    fun `Verify findByName JSON API doc`() {
        client.get().uri("/api/cows/{name}", "Marguerite")
                .exchange()
                .expectBody()
                .consumeWith(document("findByNameCow", responseFields(*cowFields())))
    }

    @Test
    fun `Verify findAll JSON API doc`() {
        client.get().uri("/api/cows/")
                .exchange()
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
