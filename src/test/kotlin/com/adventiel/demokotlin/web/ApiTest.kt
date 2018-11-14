package com.adventiel.demokotlin.web

import com.adventiel.demokotlin.model.Cow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {

    @Autowired
    private lateinit var client: WebTestClient

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
