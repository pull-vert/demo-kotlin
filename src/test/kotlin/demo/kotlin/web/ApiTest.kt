package demo.kotlin.web

import com.fasterxml.jackson.databind.ObjectMapper
import demo.kotlin.entities.Role
import demo.kotlin.entities.Role.Companion.ROLE_ADMIN
import demo.kotlin.entities.Role.Companion.ROLE_USER
import demo.kotlin.repositories.CowRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.services.AuthenticatedUser
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.constraints.ConstraintDescriptions
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.ExchangeStrategies
import reactor.netty.http.client.HttpClient
import java.lang.IllegalArgumentException

internal typealias ServerResponseError = Map<String, Any>

@ExtendWith(RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs(
        uriScheme = "https",
        uriPort = 8443
)
internal abstract class ApiTest {

    protected lateinit var client: WebTestClient
    private lateinit var jwtUtil: JWTUtil
    protected lateinit var cowRepository: CowRepository

    @BeforeAll
    fun before(
            @Autowired jwtUtil: JWTUtil,
            @Autowired restDocumentation: RestDocumentationContextProvider,
            @Autowired objectMapper: ObjectMapper,
            @Autowired cowRepository: CowRepository,
            @LocalServerPort port: Int
    ) {
        this.jwtUtil = jwtUtil
        this.cowRepository = cowRepository
        val sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build()
        val httpClient = HttpClient.create().secure { t -> t.sslContext(sslContext) }
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
        val user = when (role) {
            ROLE_USER -> AuthenticatedUser("Fred", "password", mutableListOf(ROLE_USER), true)
            ROLE_ADMIN -> AuthenticatedUser("Boss", "secured_password", mutableListOf(ROLE_ADMIN), true)
            else -> throw IllegalArgumentException()
        }
        val jwtToken = jwtUtil.generateToken(user)
        return "Bearer $jwtToken"
    }

    /**
     * Extension function to [WebTestClient.RequestHeadersSpec] allowing to add
     * a jwt token HTTP Header -> Authorization : Bearer JwtToken
     * Can be used only in Tests extending [ApiTest]
     */
    protected fun WebTestClient.RequestHeadersSpec<*>.addAuthHeader(role: Role = ROLE_USER) =
            this.header(HttpHeaders.AUTHORIZATION, buildAuthHeader(role))

    protected class ConstrainedFields internal constructor(input: Class<*>) {

        private val constraintDescriptions = ConstraintDescriptions(input)

        internal fun withPath(path: String) =
                fieldWithPath(path).attributes(key("constraints").value(
                        StringUtils.collectionToDelimitedString(
                                this.constraintDescriptions.descriptionsForProperty(path), "\n", "* ", "")))
    }
}
