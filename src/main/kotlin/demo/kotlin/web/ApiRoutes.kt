package demo.kotlin.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router


@Configuration
class ApiRoutes(
        private val cowHandler: CowHandler,
        private val authenticationHandler: AuthenticationHandler,
        private val userHandler: UserHandler
) {

    @Bean
    fun appRouter() = router {
        accept(APPLICATION_JSON).nest {
            "/api".nest {
                "/cows".nest {
                    GET("/{name}", cowHandler::findByName)
                    GET("/", cowHandler::findAll)
                }
                "/users".nest {
                    DELETE("/{userId}", userHandler::delete)
                }
            }
            "/auth".nest {
                POST("/", authenticationHandler::auth)
            }
        }
    }
}
