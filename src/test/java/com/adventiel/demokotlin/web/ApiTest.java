package com.adventiel.demokotlin.web;

import com.adventiel.demokotlin.model.Cow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @LocalServerPort
    private Integer port;
    private WebClient client;

    @PostConstruct
    public void postConstruct() {
        System.out.println("random port = " + port);
        client = WebClient.create("http://localhost:" + port);
    }

    @Test
    public void verifyFindByNameJSONAPIReturnsMargerite() {
        StepVerifier.create(client.get().uri("/api/cows/Marguerite").retrieve()
                .bodyToMono(Cow.class))
                .consumeNextWith(next -> {
            assertThat(next.getName()).isEqualTo("Marguerite");
            assertThat(next.getLastCalvingDate()).isNotNull();
        }).verifyComplete();
    }

    @Test
    public void verifyFindAllJSONAPIReturns2Cows() {
        StepVerifier.create(client.get().uri("/api/cows/").retrieve()
                .bodyToFlux(Cow.class))
                .expectNextCount(2)
                .verifyComplete();
    }
}
