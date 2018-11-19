package demo.kotlin.web

import demo.kotlin.model.Cow
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import javax.annotation.PostConstruct


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest(
        @LocalServerPort val port: Int
) {

lateinit var client: WebTestClient

    @PostConstruct
    fun postConstruct() {
        // trust any CA, to communicate with self signed server keystore
        val sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()

        val tcpClient = TcpClient.create().secure{ t -> t.sslContext(sslContext) }
        val httpClient = HttpClient.from(tcpClient)
        val httpConnector = ReactorClientHttpConnector(httpClient)

        client = WebTestClient.bindToServer(httpConnector).baseUrl("https://localhost:$port").build()
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
}
