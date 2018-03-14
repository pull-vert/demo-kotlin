package com.adventiel.demokotlin.repository

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.core.IsNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.test

@RunWith(SpringRunner::class)
@SpringBootTest
class CowRepositoryTest {
    @Autowired
    private lateinit var cowRepository: CowRepository

    @Test
    fun `Assert findByName returns existing Marguerite Cow`() {
        cowRepository.findByName("Marguerite")
                .test()
                .consumeNextWith {
                    assertThat(it.name).isEqualTo("Marguerite")
                    assertThat(it.lastCalvingDate).isNotNull()
                    assertThat(it.id).isNotNull()
                }.verifyComplete()
    }

    @Test
    fun `Assert findByName returns no Paquerette Cow`() {
        cowRepository.findByName("Paquerette")
                .test()
                .verifyComplete()
    }
}