package demo.kotlin.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest
class CowRepositoryTest(@Autowired private val cowRepository: CowRepository) {

    @Test
    fun `Verify findByName returns existing Marguerite Cow`() {
        cowRepository.findByName("Marguerite")
                .test()
                .consumeNextWith { cow ->
                    assertThat(cow.name).isEqualTo("Marguerite")
                    assertThat(cow.lastCalvingDate).isNotNull()
                    assertThat(cow.id).isNotNull()
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
