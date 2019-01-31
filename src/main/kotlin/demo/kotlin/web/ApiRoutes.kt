package demo.kotlin.web

import demo.kotlin.web.handlers.AuthenticationHandler
import demo.kotlin.web.handlers.CowHandler
import demo.kotlin.web.handlers.UserHandler
import demo.kotlin.web.handlers.save
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
                    GET("/{id}", cowHandler::findById)
                    GET("/name/{name}", cowHandler::findByName)
                    GET("/", cowHandler::findAll)
                    POST("/") { cowHandler.save(it) }
                }
                "/users".nest {
                    GET("/{id}", userHandler::findById)
                    DELETE("/{id}", userHandler::deleteById)
                    POST("/") { userHandler.save(it) }
                }
            }
            "/auth".nest {
                POST("/", authenticationHandler::auth)
            }
        }
    }
}
