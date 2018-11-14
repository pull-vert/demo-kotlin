package com.adventiel.demokotlin.web;

import com.adventiel.demokotlin.model.Cow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void verifyFindByNameJSONAPIReturnsMargerite() {
        client.get().uri("/api/cows/Marguerite")
                .exchange()
                .expectBody(Cow.class)
                .consumeWith(next -> {
                    final Cow cow = next.getResponseBody();
            assertThat(cow.getName()).isEqualTo("Marguerite");
            assertThat(cow.getLastCalvingDate()).isNotNull();
        });
    }

    @Test
    public void verifyFindAllJSONAPIReturns2Cows() {
        client.get().uri("/api/cows/")
                .exchange()
                .expectBodyList(Cow.class)
                .hasSize(2);
    }
}
