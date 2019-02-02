package demo.kotlin.web

import com.fasterxml.jackson.databind.ObjectMapper
import demo.kotlin.entities.Role
import demo.kotlin.entities.Role.ROLE_ADMIN
import demo.kotlin.entities.Role.ROLE_USER
import demo.kotlin.entities.User
import demo.kotlin.security.JWTUtil
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.netty.http.client.HttpClient
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies

internal typealias ServerResponseError = Map<String, Any>

@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs(uriScheme = "https", uriPort = 8443)
internal abstract class ApiTest(
        private val port: Int,
        private val jwtUtil: JWTUtil,
        private val objectMapper: ObjectMapper
) {

    protected lateinit var client: WebTestClient

    @BeforeEach
    fun before(restDocumentation: RestDocumentationContextProvider) {
        val sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        val httpClient = HttpClient.create().secure{ t -> t.sslContext(sslContext) }
        val httpConnector = ReactorClientHttpConnector(httpClient)

        client = WebTestClient.bindToServer(httpConnector)
                .baseUrl("https://localhost:$port")
                .exchangeStrategies(
                        ExchangeStrategies.builder().codecs { codecs ->
                            val defaults = codecs.defaultCodecs()
                            defaults.jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                            defaults.jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
                        }.build())
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build()
    }

    private fun buildAuthHeader(role: Role): String {
        val user = when(role) {
            ROLE_USER -> User("Fred", "password", authorities = mutableListOf(ROLE_USER), enabled = true)
            ROLE_ADMIN -> User("Boss", "secured_password", authorities = mutableListOf(ROLE_ADMIN), enabled = true)
        }
        val jwtToken = jwtUtil.generateToken(user)
        println("generating jwt token : $jwtToken")
        return "Bearer $jwtToken"
    }

    /**
     * Extension function to [WebTestClient.RequestHeadersSpec] allowing to add
     * a jwt token HTTP Header -> Authorization : Bearer JwtToken
     * Can be used only in Tests extending [ApiTest]
     */
    protected fun WebTestClient.RequestHeadersSpec<*>.addAuthHeader(role: Role = ROLE_USER) =
            this.header(HttpHeaders.AUTHORIZATION, buildAuthHeader(role))
}
