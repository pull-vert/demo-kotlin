package demo.kotlin.repository

import demo.kotlin.app
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.getBean
import org.springframework.context.ConfigurableApplicationContext
import reactor.test.test

//private val simplifiedWebConfig = configuration {
//    server {
//        port = if (profiles.contains("test")) 8181 else 8080
//    }
//}
//
//private val dataApp = application {
//    import(dataConfig)
//    import(simplifiedWebConfig)
//}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CowRepositoryTest {

    private lateinit var context: ConfigurableApplicationContext
    private lateinit var cowRepository: CowRepository

    @BeforeAll
    fun beforeAll() {
        context = app.run(profiles = "test")
        cowRepository = context.getBean()
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }

    @Test
    fun `Verify findByName returns existing Marguerite Cow`() {
        cowRepository.findByName("Marguerite")
                .test()
                .consumeNextWith {
                    assertThat(it.name).isEqualTo("Marguerite")
                    assertThat(it.lastCalvingDate).isNotNull()
                    assertThat(it.id).isNotNull()
                }.verifyComplete()
    }

    @Test
    fun `Verify findByName returns no Paquerette Cow`() {
        cowRepository.findByName("Paquerette")
                .test()
                .verifyComplete()
    }

    @Test
    fun `Verify findAll returns 2 Cows`() {
        cowRepository.findAll()
                .test()
                .expectNextCount(2)
                .verifyComplete()
    }
}
