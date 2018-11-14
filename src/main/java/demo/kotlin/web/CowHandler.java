package demo.kotlin.web;

import demo.kotlin.model.Cow;
import demo.kotlin.repository.CowRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CowHandler {
    private CowRepository cowRepository;

    public CowHandler(CowRepository cowRepository) {
        this.cowRepository = cowRepository;
    }

    @NotNull
    Mono<ServerResponse> findByName(ServerRequest req) {
        return ok().body(cowRepository.findByName(req.pathVariable("name")), Cow.class);
    }

    @NotNull
    Mono<ServerResponse> findAll(ServerRequest req) {
        return ok().body(cowRepository.findAll(), Cow.class);
    }
}
