package com.adventiel.demokotlin.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CowRepositoryTest {
    @Autowired
    private CowRepository cowRepository;

    @Test
    public void verifyFindByNameReturnsExistingMargueriteCow() {
        StepVerifier.create(cowRepository.findByName("Marguerite"))
                .consumeNextWith(next -> {
                    assertThat(next.getName()).isEqualTo("Marguerite");
                    assertThat(next.getLastCalvingDate()).isNotNull();
                    assertThat(next.getId()).isNotNull();
                }).verifyComplete();
    }

    @Test
    public void verifyFindByNameReturnsNoPaqueretteCow() {
        StepVerifier.create(cowRepository.findByName("Paquerette"))
                .verifyComplete();
    }

    @Test
    public void verifyFindAllReturns2Cows() {
        StepVerifier.create(cowRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}
