package demo.kotlin.web

import demo.kotlin.app
import demo.kotlin.model.Cow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTest {

    private lateinit var context: ConfigurableApplicationContext
    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

    @BeforeAll
    fun beforeAll() {
        context = app.run(profiles = "test")
    }

    @AfterAll
    fun afterAll() {
        context.close()
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
