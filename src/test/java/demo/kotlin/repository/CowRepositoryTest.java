package demo.kotlin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CowRepositoryTest {
    @Autowired
    private CowRepository cowRepository;

    @DisplayName("Verify findByName returns existing Marguerite Cow")
    @Test
    void verifyFindByNameReturnsExistingMargueriteCow() {
        StepVerifier.create(cowRepository.findByName("Marguerite"))
                .consumeNextWith(next -> {
                    assertThat(next.getName()).isEqualTo("Marguerite");
                    assertThat(next.getLastCalvingDate()).isNotNull();
                    assertThat(next.getId()).isNotNull();
                }).verifyComplete();
    }

    @DisplayName("Verify findByName returns no Paquerette Cow")
    @Test
    void verifyFindByNameReturnsNoPaqueretteCow() {
        StepVerifier.create(cowRepository.findByName("Paquerette"))
                .verifyComplete();
    }

    @DisplayName("Verify findAll returns 2 Cows")
    @Test
    void verifyFindAllReturns2Cows() {
        StepVerifier.create(cowRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}
