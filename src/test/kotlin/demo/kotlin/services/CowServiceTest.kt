package demo.kotlin.services

import demo.kotlin.entities.Cow
import demo.kotlin.repositories.CowRepository
import demo.kotlin.web.NotFoundStatusException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CowServiceTest {
    private lateinit var cowService: CowService
    private lateinit var repository: CowRepository

    @BeforeEach
    private fun before(@Mock repository: CowRepository) {
        this.repository = repository
        this.cowService = CowService(repository)
    }

    @Test
    fun `Verify findBySlug return one value`() {
        val cow = Cow("Marguerite", LocalDateTime.of(2017, 9, 28, 13, 30))
        val name = "Marguerite"
        given(repository.findByName(name))
                .willReturn(cow.toMono())

        cowService.findByName(name)
                .test()
                .assertNext { message ->
                    assertThat(message).isEqualTo(cow)
                }.verifyComplete()
    }

    @Test
    fun `Verify findBySlug no value throws NotFoundStatusException`() {
        val name = "Marguerite"
        given(repository.findByName(name))
                .willReturn(Mono.empty())

        cowService.findByName(name)
                .test()
                .consumeErrorWith { thrown ->
                    assertThat(thrown).isInstanceOf(NotFoundStatusException::class.java)
                            .hasMessageContaining("404 NOT_FOUND")
                            .hasMessageContaining("No Cow found for $name name")
                }.verify()
    }
}
