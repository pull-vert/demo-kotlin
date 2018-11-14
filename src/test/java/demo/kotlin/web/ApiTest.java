package demo.kotlin.web;

import demo.kotlin.model.Cow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTest {

    @Autowired
    private WebTestClient client;

    @DisplayName("Verify findByName JSON API returns Margerite")
    @Test
    void verifyFindByNameJSONAPIReturnsMargerite() {
        client.get().uri("/api/cows/Marguerite")
                .exchange()
                .expectBody(Cow.class)
                .consumeWith(next -> {
                    final Cow cow = next.getResponseBody();
            assertThat(cow.getName()).isEqualTo("Marguerite");
            assertThat(cow.getLastCalvingDate()).isNotNull();
        });
    }

    @DisplayName("Verify findAll JSON API returns 2 Cows")
    @Test
    void verifyFindAllJSONAPIReturns2Cows() {
        client.get().uri("/api/cows/")
                .exchange()
                .expectBodyList(Cow.class)
                .hasSize(2);
    }
}
