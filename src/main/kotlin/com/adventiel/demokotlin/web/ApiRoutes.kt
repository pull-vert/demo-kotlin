package com.adventiel.demokotlin.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

@Configuration
class ApiRoutes(val cowHandler: CowHandler) {

    @Bean
    fun appRouter() = router {
        accept(APPLICATION_JSON).nest {
            "/api/cows".nest {
                GET("/{name}", cowHandler::findByName)
                GET("/", cowHandler::findAll)
            }
        }
    }
}
