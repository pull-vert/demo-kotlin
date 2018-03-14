package com.adventiel.demokotlin.web

import com.adventiel.demokotlin.model.Cow
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.test
import javax.annotation.PostConstruct

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {

    @LocalServerPort
    private var port: Int? = null
    private lateinit var client: WebClient

    @PostConstruct
    fun postConstruct() {
        println("random port = $port")
        // TODO Migrate to WebTestClient when https://youtrack.jetbrains.com/issue/KT-5464 will be fixed
        client = WebClient.create("http://localhost:$port")
    }

    @Test
    fun `Verify find by name JSON API`() {
        client.get().uri("/api/cows/Marguerite").retrieve().bodyToMono<Cow>()
                .test()
                .consumeNextWith {
                    Assertions.assertThat(it.name).isEqualTo("Marguerite")
                    Assertions.assertThat(it.lastCalvingDate).isNotNull()
                }.verifyComplete()
    }
}
