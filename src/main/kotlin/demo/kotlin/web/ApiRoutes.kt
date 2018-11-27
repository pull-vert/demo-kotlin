package demo.kotlin.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router


@Configuration
class ApiRoutes(
        val cowHandler: CowHandler,
        val authenticationHandler: AuthenticationHandler
) {

    @Bean
    fun appRouter(/*httpsRedirectFilter: HandlerFilterFunction<ServerResponse, ServerResponse>*/) = router {
        accept(APPLICATION_JSON).nest {
            "/api".nest {
                "/cows".nest {
                    GET("/{name}", cowHandler::findByName)
                    GET("/", cowHandler::findAll)
                }
            }
            "/auth".nest {
                POST("/", authenticationHandler::auth)
            }
        }
    }
}
