package demo.kotlin.web

import demo.kotlin.web.handlers.AuthenticationHandler
import demo.kotlin.web.handlers.CowHandler
import demo.kotlin.web.handlers.UserHandler
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
                    GET("/{id}", userHandler::findById)
                    DELETE("/{id}", userHandler::deleteById)
                }
            }
            "/auth".nest {
                POST("/", authenticationHandler::auth)
            }
        }
    }
}
