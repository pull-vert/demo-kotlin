package demo.kotlin.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApiRoutes {
    private CowHandler cowHandler;

    public ApiRoutes(CowHandler cowHandler) {
        this.cowHandler = cowHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> appRouter() {
        return nest(accept(APPLICATION_JSON),
                nest(path("/api/cows"),
                        route(GET("/{name}"), cowHandler::findByName)
                        .andRoute(GET("/"), cowHandler::findAll)));
    }
}
