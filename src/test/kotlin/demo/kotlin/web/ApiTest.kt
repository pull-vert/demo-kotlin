package demo.kotlin.web

import demo.kotlin.model.Cow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import javax.annotation.PostConstruct
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslContext
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
class ApiTest {

    @LocalServerPort
    private var port: Int? = null
//    @Autowired
//    private lateinit var webTestClientBuilder: WebTestClient.Builder
    @Autowired
    private lateinit var client: WebTestClient

//    @PostConstruct
//    fun postConstruct() {
//        client = webTestClientBuilder
//                .baseUrl("http://localhost:$port")
//                .build()
//    }

//    @Test
//    fun `Verify findByName JSON API returns Margerite`() {
//        client.get().uri("/api/cows/Marguerite")
//                .exchange()
//                .expectBody<Cow>()
//                .consumeWith {
//                    val cow = it.responseBody
//                    assertThat(cow?.name).isEqualTo("Marguerite")
//                    assertThat(cow?.lastCalvingDate).isNotNull()
//                }
//    }

    @Test
    fun `Verify findAll JSON API returns 2 Cows`() {
        client.get().uri("/api/cows/")
                .exchange()
                .expectBodyList<Cow>()
                .hasSize(2)
    }
}
